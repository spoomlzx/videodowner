package zlc.season.downloadx.core

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import zlc.season.downloadx.Progress
import zlc.season.downloadx.State
import zlc.season.downloadx.database.TaskInfo
import zlc.season.downloadx.helper.Default
import zlc.season.downloadx.utils.closeQuietly
import zlc.season.downloadx.utils.fileName
import zlc.season.downloadx.utils.fileType
import zlc.season.downloadx.utils.log
import java.io.File

@OptIn(ObsoleteCoroutinesApi::class, FlowPreview::class, ExperimentalCoroutinesApi::class)
open class DownloadTask(
    val coroutineScope: CoroutineScope,
    val param: DownloadParam,
    val config: DownloadConfig,
    private val stateHolder: StateHolder = StateHolder()
) {
    //private val stateHolder by lazy { StateHolder() }

    var downloadJob: Job? = null
    private var downloader: Downloader? = null

    private val downloadProgressFlow = MutableStateFlow(0)
    private val downloadStateFlow = MutableStateFlow<State>(stateHolder.currentState)

    fun isStarted(): Boolean {
        return stateHolder.isStarted()
    }

    fun isFailed(): Boolean {
        return stateHolder.isFailed()
    }

    fun isSucceed(): Boolean {
        return stateHolder.isSucceed()
    }

    fun canStart(): Boolean {
        return stateHolder.canStart()
    }

    fun checkJob() = downloadJob?.isActive == true

    /**
     * 获取下载文件
     */
    fun file(): File? {
        return if (param.saveName.isNotEmpty() && param.savePath.isNotEmpty()) {
            File(param.savePath, param.saveName)
        } else {
            null
        }
    }

    fun dir(): File? {
        return if (param.savePath.isNotEmpty()) {
            File(param.savePath)
        } else {
            null
        }
    }

    /**
     * 开始下载，添加到下载队列
     */
    fun start() {
        coroutineScope.launch {
            if (checkJob()) return@launch

            notifyWaiting()
            try {
                config.queue.enqueue(this@DownloadTask)
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    notifyFailed()
                }
                e.log()
            }
        }
    }

    /**
     * 停止下载
     */
    fun pause() {
        coroutineScope.launch {
            if (isStarted()) {
                config.queue.dequeue(this@DownloadTask)
                downloadJob?.cancel()
                notifyPaused()
            }
        }
    }

    /**
     * 开始下载并等待下载完成，直接开始下载，不添加到下载队列
     */
    suspend fun suspendStart() {
        if (checkJob()) return

        downloadJob?.cancel()
        val errorHandler = CoroutineExceptionHandler { _, throwable ->
            throwable.log()
            if (throwable !is CancellationException) {
                coroutineScope.launch {
                    notifyFailed()
                }
            }
        }
        downloadJob = coroutineScope.launch(errorHandler + Dispatchers.IO) {
            val response = config.request(param.url, Default.RANGE_CHECK_HEADER)
            try {
                if (!response.isSuccessful || response.body() == null) {
                    throw RuntimeException("request failed")
                }

                if (param.saveName.isEmpty()) {
                    param.saveName = "video." + response.fileType()
                }

                if (downloader == null) {
                    downloader = config.dispatcher.dispatch(this@DownloadTask, response)
                }

                notifyStarted()

                val deferred = async(Dispatchers.IO) { downloader?.download(param, config, response) }
                deferred.await()

                notifySucceed()
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    notifyFailed()
                }
                e.log()
            } finally {
                response.closeQuietly()
            }
        }
        downloadJob?.join()
    }

    /**
     * @param interval 更新进度间隔时间，单位ms
     * @param ensureLast 能否收到最后一个进度
     */
    fun progress(interval: Long = 200, ensureLast: Boolean = true): Flow<Progress> {
        return downloadProgressFlow.flatMapConcat {
            // make sure send once
            var hasSend = false
            channelFlow {
                while (currentCoroutineContext().isActive) {
                    val progress = getProgress()

                    if (hasSend && stateHolder.isEnd()) {
                        if (!ensureLast) {
                            break
                        }
                    }

                    send(progress)
                    //"url ${param.url} progress ${progress.percentStr()}".log()
                    hasSend = true

                    if (progress.isComplete()) break

                    delay(interval)
                }
            }
        }
    }

    /**
     * @param interval 更新进度间隔时间，单位ms
     */
    fun state(interval: Long = 200): Flow<State> {
        return downloadStateFlow.combine(progress(interval, ensureLast = false)) { l, r -> l.apply { progress = r } }
    }

    suspend fun getProgress(): Progress {
        return downloader?.queryProgress() ?: stateHolder.currentState.progress
    }

    fun getState() = stateHolder.currentState

    suspend fun notifyWaiting() {
        stateHolder.updateState(stateHolder.waiting, getProgress())
        downloadStateFlow.value = stateHolder.currentState
        config.taskManager.insertTaskInfo(buildTaskInfo())
        "url ${param.url} download task waiting.".log()
    }

    private suspend fun notifyStarted() {
        stateHolder.updateState(stateHolder.downloading, getProgress())
        downloadStateFlow.value = stateHolder.currentState
        downloadProgressFlow.value = downloadProgressFlow.value + 1
        config.taskManager.updateTaskInfo(buildTaskInfo())
        "url ${param.url} download task start.".log()
    }

    suspend fun notifyPaused() {
        stateHolder.updateState(stateHolder.paused, getProgress())
        downloadStateFlow.value = stateHolder.currentState
        config.taskManager.updateTaskInfo(buildTaskInfo())
        "url ${param.url} download task paused.".log()
    }

    suspend fun notifyFailed() {
        stateHolder.updateState(stateHolder.failed, getProgress())
        downloadStateFlow.value = stateHolder.currentState
        config.taskManager.updateTaskInfo(buildTaskInfo())
        "url ${param.url} download task failed.".log()
    }

    private suspend fun notifySucceed() {
        stateHolder.updateState(stateHolder.succeed, getProgress())
        downloadStateFlow.value = stateHolder.currentState
        config.taskManager.updateTaskInfo(buildTaskInfo())
        "url ${param.url} download task succeed.".log()
    }

    private fun Progress.isComplete(): Boolean {
        return totalSize > 0 && totalSize == downloadSize
    }


    /**
     * 从DownloadTask构造可以存入DB的TaskInfo
     */
    fun buildTaskInfo(): TaskInfo {
        val param = param
        val state = stateHolder.currentState
        return TaskInfo(
            param.tag(),
            param.saveName,
            param.savePath,
            param.extra,
            param.url,
            state.progress.downloadSize,
            state.progress.totalSize,
            System.currentTimeMillis(),
            state.status
        )
    }

    class StateHolder {
        val none by lazy { State.None() }
        val waiting by lazy { State.Waiting() }
        val downloading by lazy { State.Downloading() }
        val paused by lazy { State.Paused() }
        val failed by lazy { State.Failed() }
        val succeed by lazy { State.Succeed() }

        var currentState: State = none

        fun isStarted(): Boolean {
            return currentState is State.Waiting || currentState is State.Downloading
        }

        fun isFailed(): Boolean {
            return currentState is State.Failed
        }

        fun isSucceed(): Boolean {
            return currentState is State.Succeed
        }

        fun canStart(): Boolean {
            return currentState is State.None || currentState is State.Failed || currentState is State.Paused
        }

        fun isEnd(): Boolean {
            return currentState is State.None || currentState is State.Waiting || currentState is State.Paused || currentState is State.Failed || currentState is State.Succeed
        }

        fun updateState(new: State, progress: Progress): State {
            currentState = new.apply { this.progress = progress }
            return currentState
        }
    }
}