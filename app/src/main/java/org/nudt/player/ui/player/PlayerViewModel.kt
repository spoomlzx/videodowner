package org.nudt.player.ui.player

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import org.nudt.common.SLog
import org.nudt.player.data.api.doFailure
import org.nudt.player.data.api.doSuccess
import org.nudt.player.data.db.VideoDb
import org.nudt.player.data.model.PlayHistory
import org.nudt.player.data.model.VodInfoModel
import org.nudt.player.data.model.VodInfoModel.PlayUrl
import org.nudt.player.data.repository.VideoRepository

class PlayerViewModel(private val videoRepository: VideoRepository) : ViewModel() {
    val vodInfo = MutableLiveData<VodInfoModel>()
    val currentIndex = MutableLiveData(0)

    fun setCurrent(newIndex: Int) {
        currentIndex.postValue(newIndex)
    }


    fun fetchVideoInfo(vodId: Int) = liveData {
        videoRepository.fetchVideoInfo(vodId).collectLatest { result ->
            result.doSuccess { value ->
                vodInfo.postValue(value)
                emit(value)
            }
            result.doFailure { throwable ->
                SLog.e("error: $throwable")
            }
        }
    }

    fun savePlayHistory(duration: Long, progress: Long) {
        vodInfo.value?.apply {
            val history = PlayHistory(
                vod_id = vod_id, vod_name = vod_name, vod_pic = vod_pic, vod_remarks = vod_remarks,
                vod_index = currentIndex.value ?: 0, progress_time = progress, total_duration = duration, last_play_time = System.currentTimeMillis()
            )
            viewModelScope.launch {
                videoRepository.saveHistory(history)
            }

        }
    }
}