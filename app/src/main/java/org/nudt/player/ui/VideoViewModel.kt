package org.nudt.player.ui

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.jeffmony.downloader.model.VideoTaskItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import org.nudt.player.adapter.VideoPagingSource
import org.nudt.player.adapter.VideoSearchPagingSource
import org.nudt.player.db.VideoDb
import org.nudt.player.model.Video
import org.nudt.player.model.VideoSource
import org.nudt.player.utils.SLog
import org.nudt.player.utils.SpUtils
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern


class VideoViewModel(private val app: Application, private val db: VideoDb) : ViewModel() {

    /**
     * 网站基本地址
     */
    private val baseUrlMall9 = SpUtils.baseUrl

    private val client = OkHttpClient().newBuilder().followRedirects(false).connectTimeout(10, TimeUnit.SECONDS) //设置连接超时时间
        .writeTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS) //设置读取超时时间
        .build()

    private val patternHLS: Pattern = Pattern.compile("(?<=setVideoHLS\\(')(.+?)(?='\\);)")

    fun bindHomePage(source: Int) = Pager(config = PagingConfig(initialLoadSize = VideoPagingSource.pageSize,
        pageSize = VideoPagingSource.pageSize,
        enablePlaceholders = false), pagingSourceFactory = {
        VideoPagingSource(app, db, source)
    }).flow.cachedIn(viewModelScope)

    fun bindSearchPage(keyWord: String) = Pager(config = PagingConfig(initialLoadSize = VideoSearchPagingSource.pageSize,
        pageSize = VideoSearchPagingSource.pageSize,
        enablePlaceholders = false), pagingSourceFactory = {
        VideoSearchPagingSource(app, db, keyWord)
    }).flow.cachedIn(viewModelScope)


    /**
     * 修改收藏状态
     * @param changedFavorState 修改后的favor状态
     */
    fun setFavor(changedFavorState: Boolean, id: Int) {
        viewModelScope.launch {
            db.videoDao().updateFavor(changedFavorState, id)
        }
    }

    /**
     * 根据id 获取对应的video对象
     * @param id video id
     */
    fun getVideo(id: Int): Flow<Video> {
        return db.videoDao().getVideoById(id)
    }

    fun removeVideo(video: Video) {
        viewModelScope.launch {
            db.videoDao().removeVideo(video)
        }
    }

    /**
     * m3u8视频地址
     */
    val videoUrl: MutableLiveData<String> = MutableLiveData("")

    /**
     * 从page_url 重新加载视频地址
     * @param pageUrl 完整的视频页面地址
     */
    fun getUrl(video: Video) {
        if (video.source == VideoSource.V2048) {
            videoUrl.value = if (video.video_url == null) "" else video.video_url
        } else if (video.source == VideoSource.MALL9) {
            viewModelScope.launch(Dispatchers.IO) {
                val request = Request.Builder().url(baseUrlMall9 + video.page_url).get().build()
                SLog.d("ready to parse page url: " + baseUrlMall9 + video.page_url)
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        SLog.e("parse error: " + e.message)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val body = response.body()?.string() ?: ""
                        val matcherHLS = patternHLS.matcher(body)
                        // 提取m3u8地址
                        if (matcherHLS.find()) {
                            videoUrl.postValue(matcherHLS.group())
                        }
                    }
                })
            }
        }

    }


    /**
     * 获取收藏的video list
     */
    fun getFavoriteVideos(): Flow<MutableList<Video>> {
        return db.videoDao().getFavoriteVideos()
    }
}