package org.nudt.player.utils

object SpUtils {
    private const val API_BASE_URL = "API_BASE_URL"
    private const val PIC_BASE_URL = "PIC_BASE_URL"

    private const val isDebug = true

    private var Ip = if (isDebug) "192.168.53.43:81" else "192.168.3.74:81"

    var baseUrl by Preference(API_BASE_URL, "http://$Ip/app/public/")

    var basePicUrl by Preference(PIC_BASE_URL, "http://$Ip/")

    fun removeByKey(key: String) {
        Preference(key, "").removeKey()
    }

    fun removeByKey() {
        Preference("", "").cleanAllMMKV()
    }
}