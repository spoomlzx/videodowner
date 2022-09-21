package org.nudt.player

import android.app.Application
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.google.gson.Gson
import com.jeffmony.downloader.VideoDownloadManager
import com.jeffmony.downloader.common.DownloadConstants
import com.jeffmony.downloader.listener.DownloadListener
import com.jeffmony.downloader.model.VideoTaskItem
import com.tencent.mmkv.MMKV
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.nudt.player.di.modulePlayer
import org.nudt.player.utils.SLog
import java.io.File


class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
        initMMKV()
        initDownloader()

        initXLog()
    }


    companion object Instance {

        private var downloadListeners: MutableList<DownloadListener> = ArrayList()

        fun addListener(listener: DownloadListener) {
            downloadListeners.add(listener)
        }

        fun removeListener(listener: DownloadListener) {
            downloadListeners.remove(listener)
        }

    }

    /**
     * 初始化视频下载器
     */
    private fun initDownloader() {
        val file = File(getExternalFilesDir("Video"), "Download")
        if (!file.exists()) {
            file.mkdir()
        }
        SLog.d("cache root: " + file.absolutePath)
        // 初始化下载设置
        val config = VideoDownloadManager.Build(this).setCacheRoot(file.absolutePath)
            .setTimeOut(DownloadConstants.READ_TIMEOUT, DownloadConstants.CONN_TIMEOUT).setConcurrentCount(DownloadConstants.CONCURRENT)
            .setIgnoreCertErrors(false).setShouldM3U8Merged(true).buildConfig()
        VideoDownloadManager.getInstance().initConfig(config)
        // 设置全局下载监听
        VideoDownloadManager.getInstance().setGlobalDownloadListener(mListener)
    }

    /**
     * 设置全局的listener，负责全局性的下载监听
     */
    private val mListener: DownloadListener = object : DownloadListener() {
        override fun onDownloadDefault(item: VideoTaskItem) {
            for (listener in downloadListeners) {
                listener.onDownloadDefault(item)
            }
        }

        override fun onDownloadPending(item: VideoTaskItem) {
            for (listener in downloadListeners) {
                listener.onDownloadPending(item)
            }
        }

        override fun onDownloadPrepare(item: VideoTaskItem) {
            VideoDownloadManager.getInstance().fetchDownloadItems()
            for (listener in downloadListeners) {
                listener.onDownloadPrepare(item)
            }
        }

        override fun onDownloadStart(item: VideoTaskItem) {
            for (listener in downloadListeners) {
                SLog.d("onDownloadStart: $item.url")
                listener.onDownloadStart(item)
            }
        }

        override fun onDownloadProgress(item: VideoTaskItem) {
            for (listener in downloadListeners) {
                SLog.d("${item.fileHash} onDownloadProgress: ${item.percent}")
                listener.onDownloadProgress(item)
            }
        }

        override fun onDownloadPause(item: VideoTaskItem) {
            for (listener in downloadListeners) {
                SLog.d("onDownloadPause: $item.url")
                listener.onDownloadPause(item)
            }
        }

        override fun onDownloadError(item: VideoTaskItem) {
            for (listener in downloadListeners) {
                SLog.d("onDownloadError: $item.url")
                listener.onDownloadError(item)
            }
        }

        override fun onDownloadSuccess(item: VideoTaskItem) {
            for (listener in downloadListeners) {
                SLog.d("onDownloadSuccess: $item.url")
                val gson = Gson()
                SLog.json(gson.toJson(item), "task item")
                listener.onDownloadSuccess(item)
            }
        }
    }

    /**
     * 初始化mmkv
     */
    private fun initMMKV() {
        val rootDir = MMKV.initialize(this)
        SLog.d("mmkv root: $rootDir")
    }

    /**
     * 初始化koin
     */
    private fun initKoin() {
        startKoin {
            androidLogger()
            androidContext(this@BaseApplication)
            modules(modulePlayer)
        }
    }


    private fun initXLog() {
        val config: LogConfiguration = LogConfiguration.Builder().logLevel(LogLevel.DEBUG)
            .tag("VideoDowner").enableBorder().build()

        XLog.init(config)
    }
}