package org.nudt.player.ui.player

import androidx.lifecycle.*
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import org.nudt.common.SLog
import org.nudt.player.data.api.doFailure
import org.nudt.player.data.api.doSuccess
import org.nudt.player.data.db.VideoDb
import org.nudt.player.data.model.PlayHistory
import org.nudt.player.data.model.Video
import org.nudt.player.data.model.VideoCacheExtra
import org.nudt.player.data.model.VodInfoModel
import org.nudt.player.data.model.VodInfoModel.PlayUrl
import org.nudt.player.data.repository.VideoRepository
import zlc.season.downloadx.DownloadXManager

class PlayerViewModel(private val videoRepository: VideoRepository) : ViewModel() {
    val vodInfo = MutableLiveData<VodInfoModel>()
    val currentIndex = MutableLiveData(0)

    val videoPlayerHeight = MutableLiveData(235f)
    val recommendVideoList = MutableLiveData<List<Video>>()

    private val gson = Gson()


    @OptIn(DelicateCoroutinesApi::class)
    fun savePlayHistory(history: PlayHistory) {
        GlobalScope.launch(Dispatchers.IO) {
            videoRepository.saveHistory(history)
        }
    }

    fun setCurrent(newIndex: Int) {
        currentIndex.postValue(newIndex)
    }


    fun fetchVideoInfo(vodId: Int) = liveData {
        videoRepository.fetchVideoInfo(vodId).collectLatest { result ->
            result.doSuccess { value ->
                vodInfo.postValue(value)
                currentIndex.value = value.history?.vod_index
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

    fun cacheVideo() {
        val vod = vodInfo.value
        vod?.let {
            val pic = if (vod.vod_pic_thumb?.startsWith("http") == true) vod.vod_pic_thumb else vod.vod_pic
            val playUrl = vod.playUrlList[currentIndex.value ?: 0]
            val extra = VideoCacheExtra(vod.vod_name, pic ?: "", playUrl.name)
            DownloadXManager.download(playUrl.url, gson.toJson(extra))
        }
    }
}