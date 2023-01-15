package zlc.season.downloadx

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

    fun downloadVideo(url: String, videoName: String, videoThumb: String, subName: String, subIndex: Int, type: Int): DownloadTask {
        val task = fetchOrInsertDownloadTask(url, type, videoName, videoThumb, subName, subIndex)
        if (task.canStart())
            task.start()
        return task
    }

    fun downloadFile(url: String): DownloadTask {
        val task = fetchOrInsertDownloadTask(url, TYPE_FILE)
        if (task.canStart())
            task.start()
        return task
    }

    /**
     * 获取已经存在的任务，用于下载列表界面
     * 如果没有则返回null
     */
    fun getDownloadTask(url: String): DownloadTask? {
        // 先查询DB中是否有当前url对应的任务
        val dbTask = taskManager.findTaskInfoByUrl(url)
        //"query dbTask from db".log()
        var downloadTask: DownloadTask?
        if (dbTask != null) {
            downloadTask = queue.getDownloadTaskByTag(dbTask.task_id)
            if (downloadTask != null) {
                //"get task from queue ${downloadTask.param.tag()}".log()
                return downloadTask
            }
            // 如果正在下载队列中，则返回队列中的task，否则从dbTask一个DownloadTask
            downloadTask = buildDownloadTask(lifecycleScope, dbTask)
            // "get task from db ${downloadTask.param.tag()}".log()
            return downloadTask
        } else {
            return null
        }
    }

    /**
     * 获取任务，如果db中没有，则创建新任务
     */
    fun fetchOrInsertDownloadTask(url: String, type: Int = TYPE_VIDEO, videoName: String = "", videoThumb: String = "", subName: String = "", subIndex: Int = 0): DownloadTask {
        // 先查询DB中是否有当前url对应的任务
        val dbTask = taskManager.findTaskInfoByUrl(url)
        //"query dbTask from db".log()
        var downloadTask: DownloadTask?
        if (dbTask != null) {
            downloadTask = queue.getDownloadTaskByTag(dbTask.task_id)
            if (downloadTask != null) {
                //"get task from queue ${downloadTask.param.tag()}".log()
                return downloadTask
            }
            // 如果正在下载队列中，则返回队列中的task，否则从dbTask一个DownloadTask
            downloadTask = buildDownloadTask(lifecycleScope, dbTask)
            // "get task from db ${downloadTask.param.tag()}".log()
        } else {
            // 重新创建一个DownloadTask
            val downloadParam = DownloadParam(
                url, getDownloadsDirPath() + "/" + url.getMd5(), System.currentTimeMillis(), "", type, videoName, videoThumb, subName, subIndex
            )
            downloadTask = DownloadTask(lifecycleScope, downloadParam, DownloadConfig(taskManager, queue))
            "get new task ${downloadTask.param.tag()}".log()
            lifecycleScope.launch {
                taskManager.insertTaskInfo(downloadTask.buildTaskInfo())
            }
        }
        return downloadTask
    }

    fun pauseResumeDownloadTask(taskInfo: TaskInfo) {
        val downloadTask = queue.getDownloadTaskByTag(taskInfo.task_id) ?: buildDownloadTask(lifecycleScope, taskInfo)
        if (downloadTask.isStarted()) {
            //"stop task ${downloadTask.param.tag()}".log()
            downloadTask.pause()
        } else if (downloadTask.canStart()) {
            //"restart task ${downloadTask.param.tag()}".log()
            downloadTask.start()
        }
    }

    fun startDownloadTask(taskInfo: TaskInfo) {
        val downloadTask = queue.getDownloadTaskByTag(taskInfo.task_id) ?: buildDownloadTask(lifecycleScope, taskInfo)
        downloadTask.start()
    }

    fun pauseDownloadTask(taskInfo: TaskInfo) {
        val downloadTask = queue.getDownloadTaskByTag(taskInfo.task_id) ?: buildDownloadTask(lifecycleScope, taskInfo)
        downloadTask.pause()
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
                it.pause()
                // 然后删除下载的文件&文件夹
                if (deleteFile) {
                    it.file()?.clear()
                    it.dir()?.delete()
                }
            }
        }
    }

    fun queryUnfinishedTaskInfoFlow() = taskManager.queryUnfinishedTaskInfoFlow()

    fun queryFinishedTaskInfoFlow() = taskManager.queryFinishedTaskInfoFlow()

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
        val downloadParam = DownloadParam(
            taskInfo.url, taskInfo.file_path, taskInfo.add_time, taskInfo.file_name,
            taskInfo.type, taskInfo.video_name, taskInfo.video_thumb, taskInfo.sub_name, taskInfo.sub_index
        )
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
        return DownloadTask(coroutineScope, downloadParam, DownloadConfig(taskManager, queue), stateHolder)
    }

}