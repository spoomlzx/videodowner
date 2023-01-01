package org.nudt.player.data.api

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.nudt.player.data.model.Video
import org.nudt.player.utils.SpUtils
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface VideoApi {

    @GET("?service=App.Video.GetVodList")
    suspend fun getVideoList(@Query("type") type: Int, @Query("page") page: Int, @Query("limit") limit: Int): ResponseData<ListBean<Video>>

    /**
     * 搜索视频api
     * @param keyword 搜索关键词
     */
    @GET("?service=App.Video.SearchVod")
    suspend fun searchVideoList(@Query("keyword") keyword: String, @Query("page") page: Int, @Query("limit") limit: Int): ResponseData<ListBean<Video>>

    @GET("?service=App.Video.GetVodById")
    suspend fun getVideoById(@Query("vodId") vodId: Int): ResponseData<Video>

    @GET("?service=App.Video.GetRecommend")
    suspend fun getVideoRecommend(@Query("type") type: Int): ResponseData<ListBean<Video>>

    @POST("?service=App.Video.ReportVideoError")
    suspend fun reportVideoError(@Query("name") name: String, @Query("content") content: String): ResponseData<String>

    class ListBean<T>(
        val total: Int,
        val page: Int,
        val pageSize: Int,
        val items: List<T>
    )

    class ResponseData<T>(
        val Msg: String,
        val Code: Int,
        val Data: T
    )

    companion object {
        private val BASE_URL = SpUtils.baseApiUrl
        fun create(): VideoApi {
            val logger = HttpLoggingInterceptor { Log.d("API", it) }
            logger.level = HttpLoggingInterceptor.Level.BASIC

            val client = OkHttpClient.Builder()
                .addInterceptor(HostInterceptor())
                .addInterceptor(logger)
                .build()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(VideoApi::class.java)
        }
    }
}