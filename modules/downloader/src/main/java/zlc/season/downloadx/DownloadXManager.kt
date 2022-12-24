package zlc.season.downloadx

import android.content.*
import android.os.IBinder
import android.util.Log
import zlc.season.downloadx.core.DownloadTask
import zlc.season.downloadx.database.DownloadTaskManager
import zlc.season.downloadx.database.TaskInfo
import zlc.season.downloadx.utils.log

object DownloadXManager {

    private lateinit var downloadService: DownloadService

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            "onServiceConnected: ".log()
            val binder = service as DownloadService.DownloadServiceBinder
            downloadService = binder.getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            "onServiceDisconnected: ".log()
        }

    }

    fun getDownloadTask(url: String, extra: String): DownloadTask {
        return downloadService.getDownloadTask(url, extra)
    }

    fun download(url: String, extra: String): DownloadTask {
        return downloadService.download(url, extra)
    }

    fun pauseResumeDownloadTask(taskInfo: TaskInfo) {
        downloadService.pauseResumeDownloadTask(taskInfo)
    }

    fun startDownloadTask(taskInfo: TaskInfo) {
        downloadService.startDownloadTask(taskInfo)
    }

    fun pauseDownloadTask(taskInfo: TaskInfo) {
        downloadService.pauseDownloadTask(taskInfo)
    }

    fun removeDownloadTask(taskInfo: TaskInfo) {
        downloadService.removeDownloadTask(taskInfo)
    }

    // 获取未下载完成任务的Flow
    fun queryUnfinishedTaskInfoFlow() = downloadService.queryUnfinishedTaskInfoFlow()

    // 获取已下载完成任务的Flow
    fun queryFinishedTaskInfoFlow() = downloadService.queryFinishedTaskInfoFlow()

    // 获取后10条已下载完成任务的Flow
    fun queryFinishedTaskInfoTopFlow() = downloadService.queryFinishedTaskInfoTopFlow()


    fun initWithServiceMode(contextWrapper: ContextWrapper): DownloadXManager {
        val serviceIntent = Intent(contextWrapper, DownloadService::class.java)
        contextWrapper.apply {
            startService(serviceIntent)
            bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
        return this
    }
}