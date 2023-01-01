package org.nudt.player.data.network

import androidx.core.net.toUri
import okhttp3.Interceptor
import okhttp3.Response
import org.nudt.player.utils.SpUtils

/**
 * 动态切换retrofit中的api的baseUrl
 */
class HostInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originRequest = chain.request()
        val oldHost = originRequest.url.host//release.course.api.cniao5.com 不带scheme http的
        val oldUrlStr = originRequest.url.toString()
        //调试使用
        val newHost = SpUtils.baseUrl.toUri().host ?: oldHost
        val newUrlStr = if (newHost == oldHost) {
            oldUrlStr
        } else oldUrlStr.replace(oldHost, newHost)

        val newBuilder = originRequest.newBuilder()
//        LogUtils.i("Host 拦截器 ${originRequest.url} 拦截后 $newUrlStr")
        newBuilder.url(newUrlStr)
        return chain.proceed(newBuilder.build())
    }
}