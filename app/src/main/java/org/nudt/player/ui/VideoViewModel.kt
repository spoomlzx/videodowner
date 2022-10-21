package org.nudt.player.ui

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.nudt.player.adapter.VideoSearchPagingSource
import org.nudt.player.data.api.VideoApi
import org.nudt.player.data.api.doSuccess
import org.nudt.player.data.db.VideoDb
import org.nudt.player.data.model.Video
import org.nudt.player.data.model.VodInfoModel
import org.nudt.player.data.repository.PageKeyedRemoteMediator
import org.nudt.player.data.repository.VideoRepository


class VideoViewModel(private val app: Application, private val db: VideoDb, private val videoApi: VideoApi) : ViewModel() {

    @OptIn(ExperimentalPagingApi::class)
    fun bindHomePage(type: Int) = Pager(config = pagingConfig, remoteMediator = PageKeyedRemoteMediator(db, videoApi, type)) {
        db.videoDao().getVideoList(type)
    }.flow.cachedIn(viewModelScope)

    fun bindSearchPage(keyWord: String) = Pager(config = PagingConfig(
        initialLoadSize = VideoSearchPagingSource.pageSize,
        pageSize = VideoSearchPagingSource.pageSize,
        enablePlaceholders = false
    ), pagingSourceFactory = {
        VideoSearchPagingSource(app, db, keyWord)
    }).flow.cachedIn(viewModelScope)

    fun removeVideo(video: Video) {
        viewModelScope.launch {
            db.videoDao().removeVideo(video)
        }
    }

    /**
     * 获取收藏的video list
     */
    fun getFavoriteVideos(): Flow<MutableList<Video>>? {
        //return db.videoDao().getFavoriteVideos()
        return null
    }

    private val pagingConfig = PagingConfig(
        // 每页显示的数据的大小
        pageSize = 10,

        // 开启占位符
        enablePlaceholders = true,

        // 预刷新的距离，距离最后一个 item 多远时加载数据
        // 默认为 pageSize
        //prefetchDistance = 4,

        /**
         * 初始化加载数量，默认为 pageSize * 3
         *
         * internal const val DEFAULT_INITIAL_PAGE_MULTIPLIER = 3
         * val initialLoadSize: Int = pageSize * DEFAULT_INITIAL_PAGE_MULTIPLIER
         */
        initialLoadSize = 10
    )
}