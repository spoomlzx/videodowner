package org.nudt.player.data.model

data class VodInfoModel(

    var vod_id: Int,
    var type_id: Int,
    var type_pid: Int,
    var vod_name: String,
    var vod_actor: String,
    var vod_pic: String,
    var vod_remarks: String,
    var vod_class: String,
    var vod_content: String?,
    var vod_area: String,
    var vod_year: Int,
    var vod_score: String?,

    var playUrlList: ArrayList<PlayUrl>
)