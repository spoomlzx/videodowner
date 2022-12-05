package zlc.season.downloadx

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import zlc.season.downloadx.core.*
import zlc.season.downloadx.database.*
import zlc.season.downloadx.utils.clear
import zlc.season.downloadx.utils.getDownloadsDirPath
import zlc.season.downloadx.utils.getMd5
import zlc.season.downloadx.utils.log

class DownloadService() : LifecycleService() {
    private val taskManager by lazy { DownloadTaskManager(applicationContext) }

    // 下载队列
    private val queue: DownloadQueue = DefaultDownloadQueue.get()

    fun download(url: String, extra: String): DownloadTask {
        // 先查询DB中是否有当前url对应的任务
        val dbTask = taskManager.findTaskInfoByUrl(url)
        "query dbTask from db".log()
        var downloadTask: DownloadTask?
        if (dbTask != null) {
            downloadTask = queue.getDownloadTaskByTag(dbTask.task_id)
            if (downloadTask != null) {
                "get task from queue ${downloadTask.param.tag()}".log()
                return downloadTask
            }
            // 如果正在下载队列中，则返回队列中的task，否则从dbTask一个DownloadTask
            downloadTask = buildDownloadTask(lifecycleScope, dbTask)
            "get task from db ${downloadTask.param.tag()}".log()
            lifecycleScope.launch {
                startTask(downloadTask!!)
            }
        } else {
            // 重新创建一个DownloadTask
            val downloadParam = DownloadParam(url, extra, getDownloadsDirPath() + "/" + url.getMd5())
            downloadTask = DownloadTask(lifecycleScope, downloadParam, DownloadConfig(taskManager))
            "get new task ${downloadTask.param.tag()}".log()
            lifecycleScope.launch {
                taskManager.insertTaskInfo(downloadTask.buildTaskInfo())
                startTask(downloadTask)
            }
        }
        return downloadTask
    }

    fun pauseResumeDownloadTask(taskInfo: TaskInfo) {
        val downloadTask = queue.getDownloadTaskByTag(taskInfo.task_id) ?: buildDownloadTask(lifecycleScope, taskInfo)
        lifecycleScope.launch {
            if (downloadTask.isStarted()) {
                pauseTask(downloadTask)
            } else if (downloadTask.canStart()) {
                startTask(downloadTask)
            }
        }
    }

    fun startDownloadTask(taskInfo: TaskInfo) {
        val downloadTask = queue.getDownloadTaskByTag(taskInfo.task_id) ?: buildDownloadTask(lifecycleScope, taskInfo)
        lifecycleScope.launch {
            startTask(downloadTask)
        }
    }

    fun pauseDownloadTask(taskInfo: TaskInfo) {
        val downloadTask = queue.getDownloadTaskByTag(taskInfo.task_id) ?: buildDownloadTask(lifecycleScope, taskInfo)
        lifecycleScope.launch {
            pauseTask(downloadTask)
        }
    }

    /**
     * 移除download task
     * @param taskInfo 任务信息
     * @param deleteFile 是否同时删除下载的文件
     */
    fun removeDownloadTask(taskInfo: TaskInfo, deleteFile: Boolean = true) {
        lifecycleScope.launch(Dispatchers.IO) {
            taskManager.deleteTaskInfo(taskInfo)
            val downloadTask = queue.getDownloadTaskByTag(taskInfo.task_id) ?: buildDownloadTask(lifecycleScope, taskInfo)
            downloadTask.let {
                // 先暂停任务，移出下载队列
                pauseTask(it)
                // 然后删除下载的文件&文件夹
                if (deleteFile) {
                    it.file()?.clear()
                    it.dir()?.delete()
                }
            }
        }
    }

    private suspend fun startTask(downloadTask: DownloadTask) {
        if (downloadTask.checkJob()) return
        downloadTask.notifyWaiting()
        try {
            queue.enqueue(downloadTask)
        } catch (e: Exception) {
            if (e !is CancellationException) {
                downloadTask.notifyFailed()
            }
            e.log()
        }
    }

    private suspend fun pauseTask(downloadTask: DownloadTask) {
        if (downloadTask.isStarted()) {
            queue.dequeue(downloadTask)
            downloadTask.downloadJob?.cancel()
            downloadTask.notifyPaused()
        }
    }


    fun queryUnfinishedTaskInfo() = taskManager.queryUnfinishedTaskInfo()

    fun queryUnfinishedTaskInfoFlow() = taskManager.queryUnfinishedTaskInfoFlow()

    fun queryFinishedTaskInfoFlow() = taskManager.queryFinishedTaskInfoFlow()

    fun queryFinishedTaskInfoTopFlow() = taskManager.queryFinishedTaskInfoTopFlow()


    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return DownloadServiceBinder()

    }

    inner class DownloadServiceBinder : Binder() {
        fun getService() = this@DownloadService
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        "onStartCommand: ".log()
        return START_NOT_STICKY
    }

    override fun onUnbind(intent: Intent?): Boolean {
        "onUnbind: ".log()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        "onDestroy: ".log()
    }


    /**
     * 从已有的taskInfo初始化DownloadTask
     */
    private fun buildDownloadTask(coroutineScope: CoroutineScope, taskInfo: TaskInfo): DownloadTask {
        val downloadParam = DownloadParam(taskInfo.url, taskInfo.extra, taskInfo.file_path, taskInfo.file_name)
        val stateHolder = DownloadTask.StateHolder()
        val state = when (taskInfo.status) {
            STATUS_NONE -> State.None()
            STATUS_WAITING -> State.Waiting()
            STATUS_DOWNLOADING -> State.Downloading()
            STATUS_PAUSED -> State.Paused()
            STATUS_SUCCEED -> State.Succeed()
            STATUS_FAILED -> State.Failed()
            else -> State.None()
        }
        stateHolder.updateState(state, Progress(taskInfo.downloaded_bytes, taskInfo.total_bytes))
        return DownloadTask(coroutineScope, downloadParam, DownloadConfig(taskManager), stateHolder)
    }

}