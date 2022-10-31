package org.nudt.player.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import org.nudt.player.data.model.FavoriteVideo

@Dao
interface FavoriteVideoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favoriteVideo: FavoriteVideo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavorite(favoriteVideo: FavoriteVideo)
}