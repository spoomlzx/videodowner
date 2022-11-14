package org.nudt.player.data.model

import org.nudt.player.utils.VideoUtil

data class VodInfoModel(

    var vod_id: Int,
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
    var playUrlList: ArrayList<PlayUrl>,
    var history: PlayHistory?
) {
    data class PlayUrl(
        var name: String,
        var url: String
    )

    companion object {
        fun fromVideo(video: Video, history: PlayHistory?): VodInfoModel {
            return video.run {
                val playUrlList = VideoUtil.convertPlayUrlList(vod_play_url)
                VodInfoModel(
                    vod_id, type_id, type_pid, vod_name, vod_actor, vod_director, vod_pic, vod_remarks, vod_class,
                    vod_content, vod_area, vod_lang, vod_year, vod_score, vod_time, vod_time_add, playUrlList, history
                )
            }
        }
    }
}