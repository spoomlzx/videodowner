package org.nudt.player.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.nudt.player.adapter.VideoSearchPagingSource
import org.nudt.player.data.db.VideoDb
import org.nudt.player.data.model.FavoriteVideo
import org.nudt.player.data.model.PlayHistory
import org.nudt.player.data.model.Video
import org.nudt.player.data.model.VodInfoModel
import org.nudt.player.data.network.VideoApi
import org.nudt.player.data.network.VideoResult

class VideoRepository(val db: VideoDb, private val videoApi: VideoApi) {

    /**
     * 根据type获取视频列表的分页数据
     */
    @OptIn(ExperimentalPagingApi::class)
    fun getVideoPage(type: Int) = Pager(config = pagingConfig, remoteMediator = PageKeyedRemoteMediator(db, videoApi, type)) {
        db.videoDao().getVideoList(type)
    }.flow


    /**
     * 根据keyword获取搜索的分页数据
     */
    fun getSearchPage(keyword: String) = Pager(pagingConfig, pagingSourceFactory = {
        VideoSearchPagingSource(videoApi, keyword)
    }).flow

    /**
     * 根据vodId获取视频info的flow
     */
    suspend fun fetchVideoInfo(vodId: Int): Flow<VideoResult<VodInfoModel>> {
        return flow {
            try {
                val videoDao = db.videoDao()
                var videoInfo = videoDao.getVideoById(vodId)
                // 如果数据库中没有，则从网络拉取
                if (null == videoInfo) {
                    videoInfo = videoApi.getVideoById(vodId).data
                    // 并存储到数据库中
                    videoDao.insert(videoInfo)
                }
                val playHistoryDao = db.playHistoryDao()
                val history = playHistoryDao.getHistoryById(vodId)


                emit(VideoResult.Success(VodInfoModel.fromVideo(videoInfo, history)))
            } catch (e: Exception) {
                emit(VideoResult.Failure(e.cause))
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getVideoRecommend(type: Int): Flow<VideoResult<List<Video>>> {
        return flow {
            try {
                val videoList = videoApi.getVideoRecommend(type).data.items
                emit(VideoResult.Success(videoList))
            } catch (e: Exception) {
                emit(VideoResult.Failure(e.cause))
            }
        }.flowOn(Dispatchers.IO)
    }

    /**
     * 发布一条视频报错信息
     */
    suspend fun reportVideoError(name: String, content: String) = videoApi.reportVideoError(name, content)

    /**
     * 获取最新版本
     */
    suspend fun checkUpdate() = videoApi.checkUpdate()

    /**
     * 保存视频观看记录
     */
    suspend fun saveHistory(history: PlayHistory) {
        db.playHistoryDao().insert(history)
    }

    /**
     * 根据id获取本地数据库中记录的播放历史
     */
    fun getHistoryById(vodId: Int): PlayHistory? {
        val playHistoryDao = db.playHistoryDao()
        return playHistoryDao.getHistoryById(vodId)
    }

    /**
     * 获取最新10条播放记录
     */
    fun getHistoryTop() = db.playHistoryDao().getHistoryTop()

    /**
     * 获取所有播放记录
     */
    fun getHistory() = db.playHistoryDao().getHistory()

    suspend fun deleteHistory(history: PlayHistory) = db.playHistoryDao().deleteHistory(history)


    suspend fun addFavorite(favoriteVideo: FavoriteVideo) {
        db.favoriteVideoDao().insert(favoriteVideo)
    }

    fun getFavoriteById(vodId: Int) = db.favoriteVideoDao().getFavoriteById(vodId)

    /**
     * 获取所有收藏内容
     */
    fun getFavorites() = db.favoriteVideoDao().getFavorites()

    /**
     * 删除一条收藏记录
     */
    suspend fun deleteFavorite(vodId: Int) = db.favoriteVideoDao().deleteFavorite(vodId)


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

        /**
         * 初始化加载数量，默认为 pageSize * 3
         *
         * internal const val DEFAULT_INITIAL_PAGE_MULTIPLIER = 3
         * val initialLoadSize: Int = pageSize * DEFAULT_INITIAL_PAGE_MULTIPLIER
         */
        initialLoadSize = 10
    )
}