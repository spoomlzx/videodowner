package org.nudt.player.di

import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.nudt.player.db.VideoDb
import org.nudt.player.ui.VideoViewModel
import org.nudt.player.ui.download.VideoTaskViewModel

val modulePlayer = module {

    viewModel {
        VideoViewModel(get(), get())
    }

    viewModel{
        VideoTaskViewModel()
    }

    single { VideoDb.initDataBase(androidApplication()) }
}