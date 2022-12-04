package zlc.season.downloadx.database

import androidx.room.*

@Dao
interface TaskInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg taskInfo: TaskInfo)

    @Delete
    suspend fun delete(vararg taskInfo: TaskInfo): Int

    @Update
    suspend fun update(vararg taskInfo: TaskInfo)

    @Query("select * from TaskInfo where url=:url")
    fun findByUrl(url: String): TaskInfo?
}