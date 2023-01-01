package org.nudt.player.utils

import org.nudt.common.Preference

object SpUtils {
    private const val BASE_URL = "BASE_URL"
    private const val PIC_BASE_URL = "PIC_BASE_URL"
    private const val VIDEO_BASE_URL = "VIDEO_BASE_URL"

    private const val isDebug = true

    //private var Ip = if (isDebug) "192.168.3.8" else "192.168.3.74:81"

    //var ip by Preference("IP", "192.168.0.173")
    var baseUrl by Preference(BASE_URL, "http://192.168.0.173")

    //var baseUrl by Preference(API_BASE_URL, "http://$Ip/app/public/")
    val baseApiUrl = "$baseUrl/app/public/"

    //var basePicUrl by Preference(PIC_BASE_URL, "http://$Ip/")
    val basePicUrl = "$baseUrl/"

    private const val USER_NICK_NAME = "USER_NICK_NAME"
    var userNickName by Preference(USER_NICK_NAME, "游客")
    var username by Preference("USER_NAME", "spoom")


    fun removeByKey(key: String) {
        Preference(key, "").removeKey()
    }

    fun removeALlKey() {
        Preference("", "").cleanAllMMKV()
    }
}