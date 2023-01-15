package zlc.season.downloadx.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg taskInfo: TaskInfo)

    @Delete
    suspend fun delete(vararg taskInfo: TaskInfo): Int

    @Update
    suspend fun update(vararg taskInfo: TaskInfo)

    @Query("select * from TaskInfo where status <> $STATUS_SUCCEED and type=0 order by add_time")
    fun queryUnfinishedTaskInfoFlow(): Flow<List<TaskInfo>>

    @Query("select * from TaskInfo where status = $STATUS_SUCCEED and type=0 order by video_name, sub_index, modify_time")
    fun queryFinishedTaskInfoFlow(): Flow<List<TaskInfo>>

    @Query("select * from TaskInfo where url=:url")
    fun findByUrl(url: String): TaskInfo?
}