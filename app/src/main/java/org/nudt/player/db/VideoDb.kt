package org.nudt.player.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.nudt.player.model.Video

@Database(
    entities = [Video::class],
    version = 1,
    exportSchema = false
)
abstract class VideoDb : RoomDatabase() {

    companion object {
        private const val DB_NAME = "video.db"
        private var instance: VideoDb? = null

        @Synchronized
        fun initDataBase(context: Context): VideoDb? {
            if (instance == null) {
                // 从video.db初始化数据库
                instance = Room.databaseBuilder(context, VideoDb::class.java, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance
        }
    }

    abstract fun videoDao(): VideoDao
}