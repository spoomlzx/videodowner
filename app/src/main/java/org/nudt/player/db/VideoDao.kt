package org.nudt.player.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.nudt.player.model.Video

@Dao
interface VideoDao {

    /**
     * 获取视频列表
     * @param start 起始位置
     * @param size 每页数量
     */
    @Query("SELECT * FROM video where source = :source order by random() limit :start,:size")
    suspend fun getVideoList(start: Int, size: Int, source: Int): List<Video>?

    /**
     * 获取视频总数
     */
    @Query("select count(*) from video where source = :source")
    suspend fun getTotal(source: Int): Int

    /**
     * 搜索视频
     * @param start
     * @param size
     * @param keyWord 关键词
     */
    @Query("SELECT * FROM video where title like '%' || :keyWord || '%' limit :start,:size")
    suspend fun getVideoListByKeyWord(start: Int, size: Int, keyWord: String): List<Video>?

    /**
     * 获取搜索到的视频总数
     */
    @Query("select count(*) from video where title like '%' || :keyWord || '%'")
    suspend fun getSearchTotal(keyWord: String): Int

    /**
     * 通过id获取视频信息
     * @param id 视频id
     */
    @Query("select * from video where id=:id")
    fun getVideoById(id: Int): Flow<Video>

    @Delete
    suspend fun removeVideo(video: Video)

    /**
     * 更新视频的收藏状态
     * @param favor 收藏状态
     * @param id 视频id
     */
    @Query("update video set favor=:favor where id=:id")
    suspend fun updateFavor(favor: Boolean, id: Int)

    /**
     * 获取收藏视频列表
     */
    @Query("select * from video where favor=1")
    fun getFavoriteVideos(): Flow<MutableList<Video>>
}