package org.nudt.player.utils

object SpUtils {
    private const val API_BASE_URL = "API_BASE_URL"
    private const val PIC_BASE_URL = "PIC_BASE_URL"

    var baseUrl by Preference(API_BASE_URL, "http://192.168.3.74:81/app/public/")

    var basePicUrl by Preference(PIC_BASE_URL, "http://192.168.3.74:81/")

    fun removeByKey(key:String){
        Preference(key, "").removeKey()
    }

    fun removeByKey(){
        Preference("", "").cleanAllMMKV()
    }
}