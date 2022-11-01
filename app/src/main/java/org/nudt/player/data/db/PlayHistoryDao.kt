package org.nudt.player.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.nudt.player.data.model.PlayHistory

@Dao
interface PlayHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(playHistory: PlayHistory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistory(playHistory: PlayHistory)

    @Query("select * from play_history where vod_id = :vodId")
    fun getHistoryById(vodId: Int): PlayHistory?
}