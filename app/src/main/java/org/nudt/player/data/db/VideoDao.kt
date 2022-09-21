package org.nudt.player.data.db

import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.nudt.player.data.model.Video

@Dao
interface VideoDao {


    @Query("SELECT * FROM video WHERE type_pid = :type")
    fun getVideoList(type: Int): PagingSource<Int, Video>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(videos: List<Video>)

    @Query("DELETE FROM video WHERE type_pid = :type")
    suspend fun deleteByType(type: Int)

    /**
     * 搜索视频
     * @param start
     * @param size
     * @param keyWord 关键词
     */
    @Query("SELECT * FROM video where vod_name like '%' || :keyWord || '%' limit :start,:size")
    suspend fun getVideoListByKeyWord(start: Int, size: Int, keyWord: String): List<Video>?

    /**
     * 获取搜索到的视频总数
     */
    @Query("select count(*) from video where vod_name like '%' || :keyWord || '%'")
    suspend fun getSearchTotal(keyWord: String): Int

    @Delete
    suspend fun removeVideo(video: Video)

    /**
     * 更新视频的收藏状态
     * @param favor 收藏状态
     * @param id 视频id
     */
    @Query("update video set favor=:favor where vod_id=:id")
    suspend fun updateFavor(favor: Boolean, id: Int)

    /**
     * 获取收藏视频列表
     */
    @Query("select * from video where favor=1")
    fun getFavoriteVideos(): Flow<MutableList<Video>>
}