package org.nudt.player.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import org.nudt.player.data.db.VideoDao
import org.nudt.player.data.db.VideoDb
import org.nudt.player.data.db.VideoRemoteKeyDao
import org.nudt.player.data.model.Video
import org.nudt.player.data.model.VideoRemoteKey
import org.nudt.player.data.network.VideoApi
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class PageKeyedRemoteMediator(
    private val db: VideoDb,
    private val videoApi: VideoApi,
    private val type: Int
) : RemoteMediator<Int, Video>() {
    private val videoDao: VideoDao = db.videoDao()
    private val remoteKeyDao: VideoRemoteKeyDao = db.videoRemoteKeyDao()

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Video>): MediatorResult {
        try {
            val pageKey = when (loadType) {
                // 首次访问 或者调用 PagingDataAdapter.refresh()
                LoadType.REFRESH -> null

                // 在当前加载的数据集的开头加载数据时
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)

                LoadType.APPEND -> { // 下来加载更多时触发
                    val remoteKey = db.withTransaction {
                        remoteKeyDao.remoteKeyByType(type)
                    }
                    if (remoteKey.nextPageKey == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    remoteKey.nextPageKey
                }
            }
            // 如果pagekey为空，则从第一页开始加载
            val page = pageKey ?: 1
            val data = videoApi.getVideoList(
                type, page, limit = when (loadType) {
                    LoadType.REFRESH -> state.config.pageSize
                    else -> state.config.pageSize
                }
            ).Data

            val videos = data.items
            val retPage = data.page
            val retPageSize = data.pageSize
            val retTotal = data.total

            // 如果未加载到数据或加载的数据不满一页，说明已经没有更多了
            val endOfPaginationReached = retPage * retPageSize >= retTotal

            db.withTransaction {
                // 更新时删除所有数据和key
                if (loadType == LoadType.REFRESH) {
                    videoDao.deleteByType(type)
                    remoteKeyDao.deleteByType(type)
                }
                // 如果已经没有更多数据则为null，有的话key加一页
                val nextKey = if (endOfPaginationReached) null else page + 1
                remoteKeyDao.insert(VideoRemoteKey(type, nextKey))
                videoDao.insertAll(videos)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }

}