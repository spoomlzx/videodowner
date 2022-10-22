package org.nudt.player.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import org.nudt.player.data.model.PlayHistory

@Dao
interface PlayHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(playHistory: PlayHistory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistory(playHistory: PlayHistory)
}