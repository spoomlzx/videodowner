package org.nudt.player.ui.player

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.flow.collectLatest
import org.koin.java.KoinJavaComponent.inject
import org.nudt.common.SLog
import org.nudt.player.data.api.doFailure
import org.nudt.player.data.api.doSuccess
import org.nudt.player.data.model.VodInfoModel.PlayUrl
import org.nudt.player.data.model.VodInfoModel
import org.nudt.player.data.repository.VideoRepository

class PlayerViewModel(private val videoRepository: VideoRepository) : ViewModel() {
    val vodInfo = MutableLiveData<VodInfoModel>()
    val currentPlayUrl = MutableLiveData<PlayUrl>()

    fun setVodInfo(vod: VodInfoModel) {
        vodInfo.postValue(vod)
    }

    fun setPlayUrl(playUrl: PlayUrl) {
        currentPlayUrl.value = playUrl
    }


    fun fetchVideoInfo(vodId: Int) = liveData<VodInfoModel> {
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
}