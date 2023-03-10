package org.nudt.player.ui.player

import androidx.lifecycle.*
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import org.nudt.common.log
import org.nudt.player.data.model.*
import org.nudt.player.data.network.ResultState
import org.nudt.player.data.network.doFailure
import org.nudt.player.data.network.doSuccess
import org.nudt.player.data.network.request
import org.nudt.player.data.repository.VideoRepository
import org.nudt.player.utils.VideoUtil
import zlc.season.downloadx.DownloadXManager

class PlayerViewModel(private val videoRepository: VideoRepository) : ViewModel() {
    private val gson = Gson()

    val vodInfo = MutableLiveData<VodInfoModel>()
    val currentIndex = MutableLiveData(0)

    val recommendVideoList = MutableLiveData<List<Video>>()

    val reportResult = MutableLiveData<ResultState<String>>()


    @OptIn(DelicateCoroutinesApi::class)
    fun savePlayHistory(history: PlayHistory) {
        GlobalScope.launch(Dispatchers.IO) {
            videoRepository.saveHistory(history)
        }
    }

    fun addFavorite(favoriteVideo: FavoriteVideo) {
        viewModelScope.launch(Dispatchers.IO) {
            videoRepository.addFavorite(favoriteVideo)
        }
    }

    fun getFavoriteById(vodId: Int): LiveData<FavoriteVideo?> = videoRepository.getFavoriteById(vodId).asLiveData()


    fun deleteFavorites(vodId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            videoRepository.deleteFavorite(vodId)
        }
    }

    fun setCurrent(newIndex: Int) {
        currentIndex.postValue(newIndex)
    }


    fun fetchVideoInfo(vodId: Int) = liveData {
        videoRepository.fetchVideoInfo(vodId).collectLatest { result ->
            result.doSuccess { value ->
                vodInfo.postValue(value)
                value.history?.let {
                    currentIndex.value = it.vod_index
                }

                "video: ${value.vod_name} index: ${value.history?.vod_index}".log()
                emit(value)
            }
            result.doFailure { throwable ->
                throwable.log()
            }
        }
    }


    fun getVideoRecommend(type: Int) {
        viewModelScope.launch {
            videoRepository.getVideoRecommend(type).collectLatest { result ->
                result.doSuccess { value ->
                    recommendVideoList.postValue(value)
                }
                result.doFailure { throwable ->
                    throwable.log()
                }
            }
        }
    }

    fun reportVideoError(name: String, content: String) {
        request({ videoRepository.reportVideoError(name, content) }, reportResult)
    }


    fun cacheVideo(): Boolean {
        val vod = vodInfo.value
        vod?.let {
            val index = currentIndex.value ?: 0
            val subVideo = vod.subVideoList[index]
            if (VideoUtil.checkMedia(subVideo.sub_video_url)) {
                DownloadXManager.downloadVideo(subVideo.sub_video_url, vod.vod_name, subVideo.sub_video_pic ?: "", subVideo.sub_video_name, index)
                return true
            }
        }
        return false
    }
}