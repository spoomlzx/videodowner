package org.nudt.player.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.nudt.player.data.api.VideoApi
import org.nudt.player.data.api.VideoResult
import org.nudt.player.data.db.VideoDb
import org.nudt.player.data.model.VodInfoModel

class VideoRepository(val db: VideoDb, private val videoApi: VideoApi) {

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

                emit(VideoResult.Success(VodInfoModel.fromVideo(videoInfo)))
            } catch (e: Exception) {
                emit(VideoResult.Failure(e.cause))
            }
        }.flowOn(Dispatchers.IO)
    }
}