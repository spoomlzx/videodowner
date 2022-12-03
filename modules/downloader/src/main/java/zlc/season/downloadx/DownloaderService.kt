package zlc.season.downloadx

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import zlc.season.downloadx.core.DownloadTask

class DownloaderService() : LifecycleService() {
    val tag = "DownloaderService"

    override fun onCreate() {
        super.onCreate()
        //appContext = applicationContext
    }

    fun download(url: String): DownloadTask {
        val task = lifecycleScope.download(url)
        return task
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
}