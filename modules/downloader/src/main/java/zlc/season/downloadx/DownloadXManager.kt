package zlc.season.downloadx

import android.content.*
import android.os.IBinder
import zlc.season.downloadx.core.DownloadTask
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

    fun getDownloadTask(url: String): DownloadTask? {
        return downloadService.getDownloadTask(url)
    }

    fun fetchOrInsertDownloadTask(url: String, videoName: String, videoThumb: String, subName: String, subIndex: Int, type: Int = 0): DownloadTask {
        return downloadService.fetchOrInsertDownloadTask(url, type, videoName, videoThumb, subName, subIndex)
    }

    fun downloadVideo(url: String, videoName: String, videoThumb: String, subName: String, subIndex: Int, type: Int = 0): DownloadTask {
        return downloadService.downloadVideo(url, videoName, videoThumb, subName, subIndex, type)
    }

    fun downloadFile(url: String): DownloadTask {
        return downloadService.downloadFile(url)
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

    fun initWithServiceMode(contextWrapper: ContextWrapper): DownloadXManager {
        val serviceIntent = Intent(contextWrapper, DownloadService::class.java)
        contextWrapper.apply {
            startService(serviceIntent)
            bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
        return this
    }
}