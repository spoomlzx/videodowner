package org.nudt.common

import android.content.Context
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object CommonUtil {

    /**
     * 转换时间到显示格式
     * @param time 时间长度，单位为秒
     * @return 时间格式   01:23:43
     */
    fun transToTime(time: Float?): String {
        return if (time == null) {
            "00:00"
        } else {
            val hour = (time / 3600).toInt()
            val minute = ((time - hour * 3600) / 60).toInt()
            val second = (time - hour * 3600 - minute * 60).toInt()
            //"$hour:$minute:$second"

            LocalTime.of(hour, minute, second).format(DateTimeFormatter.ISO_TIME)
        }
    }

    fun isVideoUrl(url: String?): Boolean {
        return if (url != null) {
            url.contains(".mp4") || url.contains(".m3u8")
        } else false
    }

    /**
     * 将dp转换成px
     * @param dipValue
     * @return
     */
    fun dpToPxInt(context: Context, dipValue: Float): Int {
        val scale: Float = context.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }

    fun pxToDpInt(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }
}