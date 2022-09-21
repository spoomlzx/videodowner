package org.nudt.player.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.nudt.player.data.model.VideoRemoteKey

@Dao
interface VideoRemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(key: VideoRemoteKey)

    @Query("SELECT * FROM remote_keys WHERE type = :type")
    suspend fun remoteKeyByType(type: Int): VideoRemoteKey

    @Query("DELETE FROM remote_keys WHERE type = :type")
    suspend fun deleteByType(type: Int)
}