package zlc.season.downloadx

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zlc.season.downloadx.core.*
import zlc.season.downloadx.database.*
import zlc.season.downloadx.utils.clear
import zlc.season.downloadx.utils.getDownloadsDirPath
import zlc.season.downloadx.utils.getMd5
import zlc.season.downloadx.utils.log

class DownloaderService() : LifecycleService() {
    val tag = "DownloaderService"

    private lateinit var taskManager: DownloadTaskManager

    // 下载队列
    val queue: DownloadQueue = DefaultDownloadQueue.get()

    override fun onCreate() {
        super.onCreate()
        taskManager = DownloadTaskManager(applicationContext)
    }

    fun download(url: String, saveName: String, extra: String): DownloadTask {
        // 先查询DB中是否有当前url对应的任务
        val dbTask = taskManager.findTaskInfoByUrl(url)

        val downloadTask: DownloadTask = if (dbTask != null) {
            // 如果正在下载队列中，则返回队列中的task，否则从dbTask一个DownloadTask
            queue.getDownloadTaskByTag(dbTask.task_id) ?: buildDownloadTask(lifecycleScope, dbTask)
        } else {
            // 重新创建一个DownloadTask
            val downloadParam = DownloadParam(url, getDownloadsDirPath() + "/" + url.getMd5(), saveName, extra)
            DownloadTask(lifecycleScope, downloadParam, DownloadConfig(taskManager))
        }
        return downloadTask
    }

    fun startDownloadTask(downloadTask: DownloadTask) {
        lifecycleScope.launch {
            startTask(downloadTask)
        }
    }

    fun pauseDownloadTask(downloadTask: DownloadTask) {
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
                // 然后删除下载的文件
                if (deleteFile) {
                    it.file()?.clear()
                }
            }
        }
    }

    private suspend fun startTask(downloadTask: DownloadTask) {
        if (downloadTask.checkJob()) return
        taskManager.insertTaskInfo(downloadTask.buildTaskInfo())
        downloadTask.notifyWaiting()
        try {
            "enqueue task ${downloadTask.param.tag()}".log()
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


    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return DownloadServiceBinder()

    }

    inner class DownloadServiceBinder : Binder() {
        fun getService() = this@DownloaderService
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d(tag, "onStartCommand: ")
        return START_NOT_STICKY
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(tag, "onUnbind: ")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(tag, "onDestroy: ")
    }


    /**
     * 从已有的taskInfo初始化DownloadTask
     */
    private fun buildDownloadTask(coroutineScope: CoroutineScope, taskInfo: TaskInfo): DownloadTask {
        val downloadParam = DownloadParam(taskInfo.url, taskInfo.file_path, taskInfo.file_name, taskInfo.extra)
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