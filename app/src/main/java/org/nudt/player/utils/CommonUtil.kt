package org.nudt.player.utils

import org.nudt.player.data.model.PlayUrl
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
     * 把数据库中请求到的playUrlList转化为List<PlayUrl>
     */
    fun convertPlayUrlList(dbVodPlayUrls: String): ArrayList<PlayUrl> {
        //电影播放地址集合
        val urls: ArrayList<PlayUrl> = ArrayList()
        //先通过$$$将播放组分出来
        val vodGroup = dbVodPlayUrls.split("$$$").toTypedArray()
        val vodPlayUrls = vodGroup[0]

        if (vodPlayUrls.contains("m3u8")) {
            //多集
            if (vodPlayUrls.contains("#")) {
                val playUrls: Array<String> = vodPlayUrls.split("#").toTypedArray()
                for (i in playUrls.indices) {
                    if (playUrls[i].endsWith("m3u8")) {
                        val singleUrl = playUrls[i]
                        if (singleUrl.contains("$")) {
                            val vodData = singleUrl.split("$").toTypedArray()
                            if (vodData.size == 2) {
                                val playUrl = PlayUrl(vodData[0], vodData[1])
                                urls.add(playUrl)
                            }
                        }
                    }
                }
            } else {
                //应该是电影，只有一集
                if (vodPlayUrls.contains("$")) {
                    val vodData: Array<String> = vodPlayUrls.split("$").toTypedArray()
                    if (vodData.size == 2) {
                        val playUrl = PlayUrl(vodData[0], vodData[1])
                        urls.add(playUrl)
                    }
                }
            }
        } else if (vodPlayUrls.contains("mp4") || vodPlayUrls.contains("mkv")) {
            // 实际视频播放地址
            // 多集
            if (vodPlayUrls.contains("#")) {
                val playUrls: Array<String> = vodPlayUrls.split("#").toTypedArray()
                for (i in playUrls.indices) {
                    if (playUrls[i].endsWith("mp4") || playUrls[i].endsWith("mkv")) {
                        val singleUrl = playUrls[i]
                        if (singleUrl.contains("$")) {
                            val vodData = singleUrl.split("$").toTypedArray()
                            if (vodData.size == 2) {
                                val url: String = if (vodData[1].startsWith("http")) {
                                    vodData[1]
                                } else {
                                    SpUtils.baseVideoUrl + vodData[1]
                                }
                                val playUrl = PlayUrl(vodData[0], url)
                                urls.add(playUrl)
                            }
                        }
                    }
                }
            } else {
                //应该是电影，只有一集
                if (vodPlayUrls.contains("$")) {
                    val vodData: Array<String> = vodPlayUrls.split("$").toTypedArray()
                    if (vodData.size == 2) {
                        val url: String = if (vodData[1].startsWith("http")) {
                            vodData[1]
                        } else {
                            SpUtils.baseVideoUrl + vodData[1]
                        }
                        val playUrl = PlayUrl(vodData[0], url)
                        urls.add(playUrl)
                    }
                }
            }
        } else {
            //这种就是云播了，h5播放那种
            //多集
            if (vodPlayUrls.contains("#")) {
                val playUrls: Array<String> = vodPlayUrls.split("#").toTypedArray()
                for (i in playUrls.indices) {
                    val singleUrl = playUrls[i]
                    if (singleUrl.contains("$")) {
                        val vodData = singleUrl.replace("$", "bbb").split("bbb").toTypedArray()
                        if (vodData.size == 2) {
                            val playUrl = PlayUrl(vodData[0], vodData[1])
                            urls.add(playUrl)
                        }
                    }
                }
            } else {
                //应该是电影，只有一集
                if (vodPlayUrls.contains("$")) {
                    val vodData: Array<String> = vodPlayUrls.replace("$", "bbb").split("bbb").toTypedArray()
                    if (vodData.size == 2) {
                        val playUrl = PlayUrl(vodData[0], vodData[1])
                        urls.add(playUrl)
                    }
                }
            }
        }
        return urls
    }

}