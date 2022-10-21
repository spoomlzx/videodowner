package org.nudt.player.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/**
 * 视频基本信息
 */
@Parcelize
@Entity(tableName = "video")
data class Video(

    @PrimaryKey var vod_id: Int,
    var type_id: Int,
    var type_pid: Int,
    var vod_name: String,
    var vod_actor: String?,
    var vod_director: String?,
    var vod_pic: String,
    var vod_remarks: String,
    var vod_class: String,
    var vod_content: String?,
    var vod_area: String,
    var vod_lang: String?,
    var vod_year: Int,
    var vod_score: String?,
    var vod_time: Int,
    var vod_time_add: Int,
    var vod_play_url: String,
) : Parcelable {}