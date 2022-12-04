package zlc.season.downloadx.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TaskInfo")
data class TaskInfo(
    @PrimaryKey
    var task_id: String,
    var file_name: String,
    var file_path: String,
    var extra: String,
    var url: String,
    var downloaded_bytes: Long,
    var total_bytes: Long,
    var create_time: Long,
    var status: Int
)