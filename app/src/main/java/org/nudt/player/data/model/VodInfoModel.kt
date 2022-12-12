package org.nudt.player.data.model

import org.nudt.player.utils.VideoUtil
import org.nudt.videoplayer.model.SubVideo

data class VodInfoModel(

    var vod_id: Int,
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
    var subVideoList: ArrayList<SubVideo>,
    var history: PlayHistory?
) {
    companion object {
        fun fromVideo(video: Video, history: PlayHistory?): VodInfoModel {
            return video.run {
                val playUrlList = VideoUtil.convertPlayUrlList(video)
                VodInfoModel(
                    vod_id, type_id, type_pid, vod_name, vod_actor, vod_director, VideoUtil.getPicUrl(vod_pic), VideoUtil.getPicUrl(vod_pic_thumb),
                    VideoUtil.getPicUrl(vod_pic_slide), vod_remarks, vod_class,
                    vod_content, vod_area, vod_lang, vod_year, vod_score, vod_time, vod_time_add, playUrlList, history
                )
            }
        }
    }
}