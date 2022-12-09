package org.nudt.player.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.nudt.player.data.model.FavoriteVideo

@Dao
interface FavoriteVideoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favoriteVideo: FavoriteVideo)

    @Query("select * from favorite_video where vod_id = :vodId")
    fun getFavoriteById(vodId: Int): Flow<FavoriteVideo?>

    @Query("select * from favorite_video order by add_time desc")
    fun getFavorites(): Flow<List<FavoriteVideo>>

    @Query("delete from favorite_video where vod_id=:vodId")
    suspend fun deleteFavorite(vodId: Int): Int
}