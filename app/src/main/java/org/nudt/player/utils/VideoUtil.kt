package org.nudt.player.utils


import org.nudt.common.ratio
import org.nudt.player.data.model.PlayHistory
import org.nudt.player.data.model.VodInfoModel.PlayUrl


object VideoUtil {
    /**
     * 把数据库中请求到的playUrlList转化为List<PlayUrl>
     */
    fun convertPlayUrlList(playServer: String?, dbVodPlayUrls: String?): ArrayList<PlayUrl> {
        //电影播放地址集合
        val urls: ArrayList<PlayUrl> = ArrayList()
        if (playServer == null || dbVodPlayUrls == null) {
            return urls
        }
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
                                    playServer + vodData[1]
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
                            playServer + vodData[1]
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

    /**
     * 根据后台获取的图片url，进行服务地址拼接
     */
    fun getPicUrl(url: String?): String? {
        return if (url == null || url == "" || url.startsWith("http")) {
            url
        } else {
            SpUtils.basePicUrl + url
        }
    }

    fun buildProgressText(playHistory: PlayHistory): String {
        return if (playHistory.total_video_num > 1) {
            "看至第${playHistory.vod_index + 1}集"
        } else {
            val percent = playHistory.progress_time ratio playHistory.total_duration
            "观看至${percent}%"
        }
    }

}