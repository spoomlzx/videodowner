package zlc.season.downloadx.database

import androidx.annotation.IntDef

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