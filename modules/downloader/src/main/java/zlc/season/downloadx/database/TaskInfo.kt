package zlc.season.downloadx.database

import androidx.annotation.IntDef
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
    var modify_time: Long,
    @DownloadStatus
    var status: Int
)


const val STATUS_NONE = 0
const val STATUS_WAITING = 1
const val STATUS_DOWNLOADING = 2
const val STATUS_PAUSED = 3
const val STATUS_SUCCEED = 4
const val STATUS_FAILED = 5

@IntDef(
    STATUS_NONE,
    STATUS_DOWNLOADING,
    STATUS_WAITING,
    STATUS_PAUSED,
    STATUS_SUCCEED,
    STATUS_FAILED
)
@Retention(AnnotationRetention.SOURCE)
annotation class DownloadStatus