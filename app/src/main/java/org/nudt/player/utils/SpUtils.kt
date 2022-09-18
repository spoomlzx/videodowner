package org.nudt.player.utils

object SpUtils {
    const val KE_BASE_URL = "token"

    var baseUrl by Preference(KE_BASE_URL, "https://www.127mall9.com")

    fun removeByKey(key:String){
        Preference(key, "").removeKey()
    }

    fun removeByKey(){
        Preference("", "").cleanAllMMKV()
    }
}