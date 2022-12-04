package zlc.season.downloadx

import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Environment
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import zlc.season.downloadx.core.*
import zlc.season.downloadx.database.DownloadTaskManager

class DownloaderService() : LifecycleService() {
    val tag = "DownloaderService"

    private lateinit var taskManager: DownloadTaskManager
    //val queue: DownloadQueue = DefaultDownloadQueue.get()

    override fun onCreate() {
        super.onCreate()
        taskManager = DownloadTaskManager(applicationContext)
    }

    fun download(url: String): DownloadTask {
        val dbTask = taskManager.findTaskInfoByUrl(url)
        val downloadTask = if (dbTask != null) {
            taskManager.buildDownloadTask(lifecycleScope, dbTask)
        } else {
            val downloadParam = DownloadParam(url, "", "")
            DownloadTask(lifecycleScope, downloadParam, DownloadConfig())
        }


//        lifecycleScope.launch {
//            queue.enqueue(task)
//        }
        //queue.enqueue(task)
        return downloadTask
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