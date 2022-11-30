package org.nudt.player

import android.app.Application
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
import org.nudt.common.SLog
import java.io.File


class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
        initMMKV()
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
}