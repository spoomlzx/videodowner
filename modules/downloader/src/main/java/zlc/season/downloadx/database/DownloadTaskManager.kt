package zlc.season.downloadx.database

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import zlc.season.downloadx.Progress
import zlc.season.downloadx.core.DownloadConfig
import zlc.season.downloadx.core.DownloadParam
import zlc.season.downloadx.core.DownloadTask

const val DATABASE_NAME = "DownloadX.db"

class DownloadTaskManager(val context: Context) {
    private val database by lazy {
        Room.databaseBuilder(context, DownloaderRoomDatabase::class.java, DATABASE_NAME).allowMainThreadQueries().build()
    }

    private val dao by lazy { database.getTaskInfoDao() }

    suspend fun insertTaskInfo(taskInfo: TaskInfo) {
        dao.insert(taskInfo)
    }

    suspend fun deleteTaskInfo(taskInfo: TaskInfo): Int {
        return dao.delete(taskInfo)
    }

    suspend fun updateTaskInfo(taskInfo: TaskInfo) {
        dao.update(taskInfo)
    }

    fun findTaskInfoByUrl(url: String): TaskInfo? {
        return dao.findByUrl(url)
    }


    fun buildDownloadTask(coroutineScope: CoroutineScope, taskInfo: TaskInfo): DownloadTask {
        val downloadParam = DownloadParam(taskInfo.url, taskInfo.file_name, taskInfo.entry_path)
        val stateHolder = DownloadTask.StateHolder()
        stateHolder.updateState(stateHolder.downloading, Progress(taskInfo.downloaded_bytes, taskInfo.total_bytes))
        return DownloadTask(coroutineScope, downloadParam, DownloadConfig(), stateHolder)
    }
}