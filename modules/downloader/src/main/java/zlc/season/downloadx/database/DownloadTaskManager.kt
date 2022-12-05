package zlc.season.downloadx.database

import android.content.Context
import androidx.room.Room

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

    fun queryUnfinishedTaskInfo() = dao.queryUnfinishedTaskInfo()

    fun queryUnfinishedTaskInfoFlow() = dao.queryUnfinishedTaskInfoFlow()

    fun queryFinishedTaskInfoFlow() = dao.queryFinishedTaskInfoFlow()

    fun queryFinishedTaskInfoTopFlow() = dao.queryFinishedTaskInfoTopFlow()

    fun findTaskInfoByUrl(url: String) = dao.findByUrl(url)


}