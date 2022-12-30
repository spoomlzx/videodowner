package org.nudt.player.ui.player

import androidx.lifecycle.*
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import org.nudt.common.SLog
import org.nudt.player.data.api.doFailure
import org.nudt.player.data.api.doSuccess
import org.nudt.player.data.model.*
import org.nudt.player.data.repository.VideoRepository
import org.nudt.player.utils.VideoUtil
import zlc.season.downloadx.DownloadXManager

class PlayerViewModel(private val videoRepository: VideoRepository) : ViewModel() {
    val vodInfo = MutableLiveData<VodInfoModel>()
    val currentIndex = MutableLiveData(0)

    val recommendVideoList = MutableLiveData<List<Video>>()

    private val gson = Gson()


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

                SLog.d("video: ${value.vod_name} index: ${value.history?.vod_index}")
                emit(value)
            }
            result.doFailure { throwable ->
                SLog.e("error: $throwable")
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
                    SLog.e("error: $throwable")
                }
            }
        }

    }

    fun cacheVideo(): Boolean {
        val vod = vodInfo.value
        vod?.let {
            val subVideo = vod.subVideoList[currentIndex.value ?: 0]
            if (VideoUtil.checkMedia(subVideo.sub_video_url)) {
                val extra = VideoCacheExtra(vod.vod_name, subVideo.sub_video_pic ?: "", subVideo.sub_video_name)
                DownloadXManager.download(subVideo.sub_video_url, gson.toJson(extra))
                return true
            }
        }
        return false
    }
}