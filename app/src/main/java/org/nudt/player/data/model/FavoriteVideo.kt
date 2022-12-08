package org.nudt.player.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_video")
data class FavoriteVideo(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var vod_id: Int,
    var vod_name: String,
    var vod_pic: String?,
    var vod_pic_thumb: String?,
    var vod_pic_slide: String?,
    var vod_remarks: String?,
    var total_duration: Long,
    var last_play_time: Long,
)