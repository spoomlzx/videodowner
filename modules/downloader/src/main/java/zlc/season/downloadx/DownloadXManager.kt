package zlc.season.downloadx

import android.content.*
import android.os.IBinder
import android.util.Log
import zlc.season.downloadx.core.DownloadTask
import zlc.season.downloadx.database.TaskInfo

object DownloadXManager {

    private lateinit var downloaderService: DownloaderService

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d("Downloader", "onServiceConnected: ")
            val binder = service as DownloaderService.DownloadServiceBinder
            downloaderService = binder.getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d("Downloader", "onServiceDisconnected: ")
        }

    }


    fun download(url: String, saveName: String, extra: String): DownloadTask {
        val task = downloaderService.download(url, saveName, extra)
        downloaderService.startDownloadTask(task)
        return task
    }

    fun startDownloadTask(downloadTask: DownloadTask) {
        downloaderService.startDownloadTask(downloadTask)
    }

    fun pauseDownloadTask(downloadTask: DownloadTask) {
        downloaderService.pauseDownloadTask(downloadTask)
    }

    fun removeDownloadTask(taskInfo: TaskInfo) {
        downloaderService.removeDownloadTask(taskInfo)
    }

//    fun buildDownloadTask(taskInfo: TaskInfo): DownloadTask {
//        return downloaderService.buildDownloadTask(downloaderService.lifecycleScope, taskInfo)
//    }


    fun initWithServiceMode(contextWrapper: ContextWrapper): DownloadXManager {
        val serviceIntent = Intent(contextWrapper, DownloaderService::class.java)
        contextWrapper.apply {
            startService(serviceIntent)
            bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
        return this
    }
}