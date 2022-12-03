package zlc.season.downloadx

import android.content.ComponentName
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import zlc.season.downloadx.core.DownloadTask

object Downloader {

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


    fun download(url: String): DownloadTask {
        val task = downloaderService.download(url)
        //task.start()
        return task
    }


    fun initWithServiceMode(contextWrapper: ContextWrapper): Downloader {
        val serviceIntent = Intent(contextWrapper, DownloaderService::class.java)
        contextWrapper.apply {
            startService(serviceIntent)
            bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
        return this
    }
}