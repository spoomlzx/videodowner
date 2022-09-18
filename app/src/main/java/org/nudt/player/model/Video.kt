package org.nudt.player.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 视频基本信息
 */
@Entity(tableName = "video")
data class Video(

    @PrimaryKey var id: Int,
    var title: String?,
    var pic: String?,
    /**
     * 页面地址
     */
    var page_url: String,
    /**
     * 远程视频地址
     */
    var video_url: String?,

    /**
     * 已播放进度
     */
    var played_time: Float?,

    var source: Int = VideoSource.MALL9,

    var favor: Boolean = false,
) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readInt(),
        parcel.readString(),
        parcel.readString(), parcel.readString().toString(),
        parcel.readString(),
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readInt(),
        parcel.readByte() != 0.toByte()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(pic)
        parcel.writeString(page_url)
        parcel.writeString(video_url)
        parcel.writeValue(played_time)
        parcel.writeInt(source)
        parcel.writeByte(if (favor) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Video> {
        override fun createFromParcel(parcel: Parcel): Video {
            return Video(parcel)
        }

        override fun newArray(size: Int): Array<Video?> {
            return arrayOfNulls(size)
        }
    }
}