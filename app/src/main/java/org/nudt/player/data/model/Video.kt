package org.nudt.player.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 视频基本信息
 */
@Entity(tableName = "video")
data class Video(

    @PrimaryKey var vod_id: Int,
    var type_id: Int,
    var type_pid: Int,
    var vod_name: String,
    var vod_actor: String?,
    var vod_director: String?,
    var vod_pic: String?,
    var vod_pic_thumb: String?,
    var vod_pic_slide: String?,
    var vod_remarks: String?,
    var vod_class: String?,
    var vod_content: String?,
    var vod_area: String?,
    var vod_lang: String?,
    var vod_year: Int?,
    var vod_score: String?,
    var vod_time: Int,
    var vod_time_add: Int,
    var play_server: String?,
    var vod_play_url: String?,
)