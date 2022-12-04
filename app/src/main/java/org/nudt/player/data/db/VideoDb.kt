package org.nudt.player.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.nudt.player.data.model.FavoriteVideo
import org.nudt.player.data.model.PlayHistory
import org.nudt.player.data.model.Video
import org.nudt.player.data.model.VideoRemoteKey

@Database(
    entities = [Video::class, VideoRemoteKey::class, PlayHistory::class, FavoriteVideo::class],
    version = 1,
    exportSchema = false
)
abstract class VideoDb : RoomDatabase() {

    companion object {
        private const val DB_NAME = "video.db"
        private lateinit var instance: VideoDb

        @Synchronized
        fun initDataBase(context: Context): VideoDb {
            // 从video.db初始化数据库
            instance = Room.databaseBuilder(context, VideoDb::class.java, DB_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
            return instance
        }
    }

    abstract fun videoDao(): VideoDao
    abstract fun videoRemoteKeyDao(): VideoRemoteKeyDao
    abstract fun playHistoryDao(): PlayHistoryDao
    abstract fun favoriteVideoDao(): FavoriteVideoDao
}