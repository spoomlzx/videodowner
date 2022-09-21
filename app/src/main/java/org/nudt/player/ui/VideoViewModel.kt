package org.nudt.player.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.*
import org.nudt.player.adapter.VideoSearchPagingSource
import org.nudt.player.data.api.VideoApi
import org.nudt.player.data.db.VideoDb
import org.nudt.player.data.model.Video
import org.nudt.player.data.repository.PageKeyedRemoteMediator
import org.nudt.player.utils.SpUtils
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern


class VideoViewModel(private val app: Application, private val db: VideoDb, private val videoApi: VideoApi) : ViewModel() {

    /**
     * 网站基本地址
     */
    private val baseUrlMall9 = SpUtils.baseUrl

    private val client = OkHttpClient().newBuilder().followRedirects(false).connectTimeout(10, TimeUnit.SECONDS) //设置连接超时时间
        .writeTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS) //设置读取超时时间
        .build()

    private val patternHLS: Pattern = Pattern.compile("(?<=setVideoHLS\\(')(.+?)(?='\\);)")

    private val pagingConfig = PagingConfig(
        // 每页显示的数据的大小
        pageSize = 10,

        // 开启占位符
        enablePlaceholders = true,

        // 预刷新的距离，距离最后一个 item 多远时加载数据
        // 默认为 pageSize
        prefetchDistance = 4,

        /**
         * 初始化加载数量，默认为 pageSize * 3
         *
         * internal const val DEFAULT_INITIAL_PAGE_MULTIPLIER = 3
         * val initialLoadSize: Int = pageSize * DEFAULT_INITIAL_PAGE_MULTIPLIER
         */
        initialLoadSize = 10
    )

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


    /**
     * 修改收藏状态
     * @param changedFavorState 修改后的favor状态
     */
    fun setFavor(changedFavorState: Boolean, id: Int) {
        viewModelScope.launch {
            db.videoDao().updateFavor(changedFavorState, id)
        }
    }

    fun removeVideo(video: Video) {
        viewModelScope.launch {
            db.videoDao().removeVideo(video)
        }
    }

    /**
     * 获取收藏的video list
     */
    fun getFavoriteVideos(): Flow<MutableList<Video>> {
        return db.videoDao().getFavoriteVideos()
    }
}