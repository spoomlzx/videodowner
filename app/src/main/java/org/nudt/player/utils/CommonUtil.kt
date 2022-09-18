package org.nudt.player.utils

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

}