package org.nudt.player.data.network

import androidx.lifecycle.MutableLiveData

/**
 * Api result state
 */
sealed class ResultState<out T> {
    data class Success<out T>(val data: T) : ResultState<T>()

    data class Error(val error: ApiException) : ResultState<Nothing>()
}

inline fun <reified T> ResultState<T>.doSuccess(success: (T) -> Unit) {
    if (this is ResultState.Success) {
        success(data)
    }
}

inline fun <reified T> ResultState<T>.doFailure(failure: (ApiException) -> Unit) {
    if (this is ResultState.Error) {
        failure(error)
    }
}


/**
 * 处理返回值
 * @param result 请求结果
 */
fun <T> MutableLiveData<ResultState<T>>.paresResult(result: ResponseData<T>) {
    value = when {
        result.isSuccess() -> {
            ResultState.Success(result.Data)
        }
        else -> {
            ResultState.Error(ApiException(result.Code, result.Msg))
        }
    }
}

/**
 * 不处理返回值 直接返回请求结果
 * @param result 请求结果
 */
//fun <T> MutableLiveData<ResultState<T>>.paresResult(result: T) {
//    value = ResultState.Success(result)
//}

/**
 * 异常转换异常处理
 */
fun <T> MutableLiveData<ResultState<T>>.paresException(e: Throwable) {
    value = ResultState.Error(ExceptionHandler.handleException(e))
}
