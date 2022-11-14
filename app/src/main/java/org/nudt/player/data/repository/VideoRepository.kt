package org.nudt.player.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.nudt.player.adapter.VideoSearchPagingSource
import org.nudt.player.data.api.VideoApi
import org.nudt.player.data.api.VideoResult
import org.nudt.player.data.db.VideoDb
import org.nudt.player.data.model.PlayHistory
import org.nudt.player.data.model.VodInfoModel

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
                    videoInfo = videoApi.getVideoById(vodId).Data
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

    /**
     * 保存视频观看记录
     */
    suspend fun saveHistory(history: PlayHistory) {
        val playHistoryDao = db.playHistoryDao()
        playHistoryDao.insert(history)
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