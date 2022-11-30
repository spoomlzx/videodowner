package org.nudt.player.di

import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.nudt.player.data.api.VideoApi
import org.nudt.player.data.db.VideoDb
import org.nudt.player.data.repository.VideoRepository
import org.nudt.player.ui.AppViewModel
import org.nudt.player.ui.VideoViewModel
import org.nudt.player.ui.player.PlayerViewModel

val modulePlayer = module {

    single {
        VideoApi.create()
    }

    single {
        VideoRepository(get(), get())
    }

    viewModel {
        PlayerViewModel(get())
    }

    viewModel {
        VideoViewModel(get())
    }

    viewModel {
        AppViewModel(get(), androidApplication())
    }

    single { VideoDb.initDataBase(androidApplication()) }
}