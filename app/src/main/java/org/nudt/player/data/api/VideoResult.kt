package org.nudt.player.data.api

/**
 * 处理fetch video 成功或者失败的类
 */
sealed class VideoResult<out T> {
    data class Success<out T>(val value: T) : VideoResult<T>()

    data class Failure(val throwable: Throwable?) : VideoResult<Nothing>()
}

inline fun <reified T> VideoResult<T>.doSuccess(success: (T) -> Unit) {
    if (this is VideoResult.Success) {
        success(value)
    }
}

inline fun <reified T> VideoResult<T>.doFailure(failure: (Throwable?) -> Unit) {
    if (this is VideoResult.Failure) {
        failure(throwable)
    }
}