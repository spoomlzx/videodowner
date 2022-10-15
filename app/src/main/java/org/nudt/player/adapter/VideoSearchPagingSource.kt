package org.nudt.player.adapter

import android.app.Application
import androidx.paging.PagingSource
import androidx.paging.PagingState
import org.nudt.player.data.db.VideoDb
import org.nudt.player.data.model.Video
import org.nudt.common.SLog

class VideoSearchPagingSource(val app: Application, private val db: VideoDb, private val keyword: String) :
    PagingSource<Int, Video>() {
    companion object {
        const val pageSize = 10
    }

    override fun getRefreshKey(state: PagingState<Int, Video>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Video> {

        try {
            val page = params.key ?: 1

            val start = (page - 1) * pageSize
            val data = db.videoDao().getVideoListByKeyWord(start, pageSize, keyword)
            val total = db.videoDao().getSearchTotal(keyword)

            return if (data == null) {
                LoadResult.Error(Throwable("data empty"))
            } else {
                val next = if (page * pageSize < total) {
                    // 假如还有数据
                    page + 1
                } else {
                    null
                }
                LoadResult.Page(data = data, prevKey = null, nextKey = next)
            }


        } catch (e: Exception) {
            SLog.e(e.message ?: "error")
            return LoadResult.Error(e)
        }

    }
}