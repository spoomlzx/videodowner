package zlc.season.downloadx

import android.content.*
import android.os.IBinder
import android.util.Log
import zlc.season.downloadx.core.DownloadTask
import zlc.season.downloadx.database.TaskInfo

object DownloadXManager {

    private lateinit var downloadService: DownloadService

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d("Downloader", "onServiceConnected: ")
            val binder = service as DownloadService.DownloadServiceBinder
            downloadService = binder.getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d("Downloader", "onServiceDisconnected: ")
        }

    }


    fun download(url: String, saveName: String, extra: String): DownloadTask {
        val task = downloadService.download(url, saveName, extra)
        downloadService.startDownloadTask(task)
        return task
    }

    fun startDownloadTask(downloadTask: DownloadTask) {
        downloadService.startDownloadTask(downloadTask)
    }

    fun pauseDownloadTask(downloadTask: DownloadTask) {
        downloadService.pauseDownloadTask(downloadTask)
    }

    fun removeDownloadTask(taskInfo: TaskInfo) {
        downloadService.removeDownloadTask(taskInfo)
    }

//    fun buildDownloadTask(taskInfo: TaskInfo): DownloadTask {
//        return downloaderService.buildDownloadTask(downloaderService.lifecycleScope, taskInfo)
//    }


    fun initWithServiceMode(contextWrapper: ContextWrapper): DownloadXManager {
        val serviceIntent = Intent(contextWrapper, DownloadService::class.java)
        contextWrapper.apply {
            startService(serviceIntent)
            bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
        return this
    }
}