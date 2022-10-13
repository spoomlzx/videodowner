package org.nudt.player.data.api

import android.util.Log
import com.google.gson.Gson
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.nudt.player.data.model.Video
import org.nudt.player.utils.SpUtils
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface VideoApi {

    @GET("?service=App.Mov.GetVodList")
    suspend fun getVideoList(
        @Query("type") type: Int,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): ResponseData<List<Video>>


    class ResponseData<T>(
        val Msg: String,
        val Code: Int,
        val Data: T
    )

    companion object {
        private val BASE_URL = SpUtils.baseUrl
        fun create(): VideoApi {
            val logger = HttpLoggingInterceptor { Log.d("API", it) }
            logger.level = HttpLoggingInterceptor.Level.BASIC

            val client = OkHttpClient.Builder()
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