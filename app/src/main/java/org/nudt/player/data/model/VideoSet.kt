package org.nudt.player.data.model

import zlc.season.downloadx.database.TaskInfo

class VideoSet(
    var videoName: String,
    var videoThumb: String,
    var totalBytes: Long,
    var subVideoList: List<TaskInfo>
)