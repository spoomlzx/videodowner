package org.nudt.player.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "play_history")
data class PlayHistory(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var vod_id: Int,
    var vod_name: String,
    var vod_pic: String,
    var vod_remarks: String,
    var vod_index: Int,
    var progress_time: Long,
    var total_duration: Long,
    var last_play_time: Long,
)