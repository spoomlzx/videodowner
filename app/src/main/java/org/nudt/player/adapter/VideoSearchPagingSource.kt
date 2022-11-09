package org.nudt.player.adapter

import android.app.Application
import androidx.paging.LoadType
import androidx.paging.PagingSource
import androidx.paging.PagingState
import org.nudt.player.data.db.VideoDb
import org.nudt.player.data.model.Video
import org.nudt.common.SLog
import org.nudt.player.data.api.VideoApi

class VideoSearchPagingSource(val app: Application, private val videoApi: VideoApi, private val keyword: String) : PagingSource<Int, Video>() {
    override fun getRefreshKey(state: PagingState<Int, Video>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Video> {

        try {
            val pageSize = params.loadSize
            val page = params.key ?: 1

            val data = videoApi.searchVideoList(
                keyword, page, limit = pageSize
            ).Data


            //val data = db.videoDao().getVideoListByKeyWord(start, pageSize, keyword)
            //val total = db.videoDao().getSearchTotal(keyword)

            return if (data.items.isEmpty()) {
                LoadResult.Error(Throwable("data empty"))
            } else {
                val next = if (data.page * pageSize < data.total) {
                    // 假如还有数据
                    page + 1
                } else {
                    null
                }
                LoadResult.Page(data = data.items, prevKey = null, nextKey = next)
            }


        } catch (e: Exception) {
            SLog.e(e.message ?: "error")
            return LoadResult.Error(e)
        }

    }
}