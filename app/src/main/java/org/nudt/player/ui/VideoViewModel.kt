package org.nudt.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import org.nudt.player.data.model.PlayHistory
import org.nudt.player.data.model.Video
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


    /**
     * 获取收藏的video list
     */
    fun getFavoriteVideos(): Flow<MutableList<Video>>? {
        //return db.videoDao().getFavoriteVideos()
        return null
    }


}