package org.nudt.player.utils

import org.nudt.common.Preference

object SpUtils {
    private const val API_BASE_URL = "API_BASE_URL"
    private const val PIC_BASE_URL = "PIC_BASE_URL"
    private const val VIDEO_BASE_URL = "VIDEO_BASE_URL"

    private const val isDebug = true

    //private var Ip = if (isDebug) "192.168.3.8" else "192.168.3.74:81"

    var ip by Preference("IP", "192.168.3.5")

    //var baseUrl by Preference(API_BASE_URL, "http://$Ip/app/public/")
    val baseUrl = "http://$ip/app/public/"

    //var basePicUrl by Preference(PIC_BASE_URL, "http://$Ip/")
    val basePicUrl = "http://$ip/"

    //var baseVideoUrl by Preference(VIDEO_BASE_URL, "http://$Ip")
    val baseVideoUrl = "http://$ip"

    fun removeByKey(key: String) {
        Preference(key, "").removeKey()
    }

    fun removeALlKey() {
        Preference("", "").cleanAllMMKV()
    }

    fun setValue() {
        baseUrl
    }
}