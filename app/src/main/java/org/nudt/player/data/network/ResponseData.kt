package org.nudt.player.data.network

class ResponseData<T>(
    val Msg: String,
    val Code: Int,
    val Data: T
) {
    fun isSuccess(): Boolean {
        return 200 == Code
    }
}

class ListBean<T>(
    val total: Int,
    val page: Int,
    val pageSize: Int,
    val items: List<T>
)