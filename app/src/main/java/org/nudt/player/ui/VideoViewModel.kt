package org.nudt.player.ui

import androidx.lifecycle.*
import androidx.paging.cachedIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.nudt.player.data.model.FavoriteVideo
import org.nudt.player.data.model.PlayHistory
import org.nudt.player.data.model.Version
import org.nudt.player.data.network.ResultState
import org.nudt.player.data.network.request
import org.nudt.player.data.repository.VideoRepository


class VideoViewModel(private val videoRepository: VideoRepository) : ViewModel() {

    /**
     * 根据type获取视频列表的分页数据
     */
    fun bindHomePage(type: Int) = videoRepository.getVideoPage(type).cachedIn(viewModelScope)

    /**
     * 根据keyword获取搜索的分页数据
     */
    fun bindSearchPage(keyword: String) = videoRepository.getSearchPage(keyword).cachedIn(viewModelScope)

    val historyTop: LiveData<List<PlayHistory>> = videoRepository.getHistoryTop().asLiveData()

    val history: LiveData<List<PlayHistory>> = videoRepository.getHistory().asLiveData()

    fun deleteHistory(history: PlayHistory) {
        viewModelScope.launch(Dispatchers.IO) {
            videoRepository.deleteHistory(history)
        }
    }

    val favorites: LiveData<List<FavoriteVideo>> = videoRepository.getFavorites().asLiveData()

    fun deleteFavorites(favoriteVideo: FavoriteVideo) {
        viewModelScope.launch(Dispatchers.IO) {
            videoRepository.deleteFavorite(favoriteVideo.vod_id)
        }
    }


    val checkUpdateResult = MutableLiveData<ResultState<Version>>()

    fun checkUpdate() {
        request({ videoRepository.checkUpdate() }, checkUpdateResult)
    }

}