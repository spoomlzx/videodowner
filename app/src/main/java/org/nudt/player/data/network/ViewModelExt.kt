package org.nudt.player.data.network

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.nudt.common.log

/**
 * net request 不校验请求结果数据是否是成功
 * @param block 请求体方法
 * @param resultState 请求回调的ResultState数据
 */
fun <T> ViewModel.request(
    block: suspend () -> ResponseData<T>,
    resultState: MutableLiveData<ResultState<T>>
): Job {
    return viewModelScope.launch {
        runCatching {
            //请求体
            block()
        }.onSuccess {
            resultState.paresResult(it)
        }.onFailure {
            it.message?.log()
            //打印错误栈信息
            it.printStackTrace()
            resultState.paresException(it)
        }
    }
}