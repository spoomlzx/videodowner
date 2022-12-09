package org.nudt.player.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.nudt.player.data.model.PlayHistory

@Dao
interface PlayHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(playHistory: PlayHistory)

    /**
     * 一般不用非suspend的insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistory(playHistory: PlayHistory)

    @Query("select * from play_history where vod_id = :vodId")
    fun getHistoryById(vodId: Int): PlayHistory?

    @Query("select * from play_history order by last_play_time desc limit 10")
    fun getHistoryTop(): Flow<List<PlayHistory>>

    @Query("select * from play_history order by last_play_time desc")
    fun getHistory(): Flow<List<PlayHistory>>

    @Delete
    suspend fun deleteHistory(history: PlayHistory): Int
}