package org.nudt.player.data.network

class ResponseData<T>(
    val msg: String,
    val code: Int,
    val data: T
) {
    fun isSuccess(): Boolean {
        return 200 == code
    }
}

class ListBean<T>(
    val total: Int,
    val page: Int,
    val pageSize: Int,
    val items: List<T>
)