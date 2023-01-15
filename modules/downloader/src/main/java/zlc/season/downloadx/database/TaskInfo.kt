package zlc.season.downloadx.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TaskInfo")
data class TaskInfo(
    @PrimaryKey
    var task_id: String,
    var file_name: String,
    var file_path: String,
    var url: String,
    var downloaded_bytes: Long,
    var total_bytes: Long,
    var modify_time: Long,
    var add_time: Long,
    @DownloadStatus
    var status: Int,
    var video_name: String,
    var video_thumb: String,
    var sub_name: String,
    var sub_index: Int,
    var type: Int = 0,
)