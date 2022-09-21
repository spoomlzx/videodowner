package org.nudt.player.di

import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.nudt.player.data.api.VideoApi
import org.nudt.player.data.db.VideoDb
import org.nudt.player.ui.VideoViewModel
import org.nudt.player.ui.download.VideoTaskViewModel

val modulePlayer = module {

    single {
        VideoApi.create()
    }

    viewModel {
        VideoViewModel(get(), get(), get())
    }

    viewModel {
        VideoTaskViewModel()
    }

    single { VideoDb.initDataBase(androidApplication()) }
}