package org.nudt.player

import android.app.Application
import com.bumptech.glide.Glide
import com.tencent.mmkv.MMKV
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.nudt.common.SLog
import org.nudt.player.di.modulePlayer
import zlc.season.downloadx.DownloadXManager


class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        DownloadXManager.initWithServiceMode(this)
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