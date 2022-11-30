package org.nudt.player.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.nudt.player.data.model.PlayHistory
import org.nudt.player.data.repository.VideoRepository

class AppViewModel(private val videoRepository: VideoRepository, application: Application) : AndroidViewModel(application) {


    fun savePlayHistory(history: PlayHistory) {
        viewModelScope.launch {
            videoRepository.saveHistory(history)
        }
    }
}