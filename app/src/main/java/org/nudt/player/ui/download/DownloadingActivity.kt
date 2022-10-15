package org.nudt.player.ui.download

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.jeffmony.downloader.VideoDownloadManager
import com.jeffmony.downloader.listener.DownloadListener
import com.jeffmony.downloader.listener.IDownloadInfosCallback
import com.jeffmony.downloader.model.VideoTaskItem
import kotlinx.coroutines.launch
import org.nudt.common.SLog
import org.nudt.player.BaseApplication
import org.nudt.player.R
import org.nudt.player.adapter.DownloadingAdapter
import org.nudt.player.databinding.ActivityDownloadingBinding

class DownloadingActivity : AppCompatActivity() {
    private val binding by lazy { ActivityDownloadingBinding.inflate(layoutInflater) }

    private lateinit var adapter: DownloadingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.tbCommon.tvTitle.text = getString(R.string.main_menu_offline_downloading)
        binding.tbCommon.ivBack.setOnClickListener { finish() }

        BaseApplication.addListener(mListener)
        initRecyclerView()
        // 异步获取所有下载任务
        VideoDownloadManager.getInstance().fetchDownloadItems(mDownloadInfosCallback);
    }

    private fun initRecyclerView() {
        binding.rvVideoDownloading.layoutManager = LinearLayoutManager(this@DownloadingActivity)
        adapter = DownloadingAdapter(this@DownloadingActivity)
        binding.rvVideoDownloading.adapter = adapter

        // 关闭recyclerview更新动画，防止图片闪烁
        (binding.rvVideoDownloading.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

    }

    private var mLastProgressTimeStamp: Long = 0

    private val mListener: DownloadListener = object : DownloadListener() {
        override fun onDownloadDefault(item: VideoTaskItem) {
            SLog.d("onDownloadDefault: $item.url")
            updateVideoTaskItem(item)
        }

        override fun onDownloadPending(item: VideoTaskItem) {
            SLog.d("onDownloadPending: $item.url")
            updateVideoTaskItem(item)
        }

        override fun onDownloadPrepare(item: VideoTaskItem) {
            updateVideoTaskItem(item)
        }

        override fun onDownloadStart(item: VideoTaskItem) {
            updateVideoTaskItem(item)
        }

        /**
         * 1秒更新一次进度
         */
        override fun onDownloadProgress(item: VideoTaskItem) {
            val currentTimeStamp = System.currentTimeMillis()
            if (currentTimeStamp - mLastProgressTimeStamp > 100) {
                // notifyChanged(item)
                updateVideoTaskItem(item)
                mLastProgressTimeStamp = currentTimeStamp
            }
        }

        override fun onDownloadPause(item: VideoTaskItem) {
            updateVideoTaskItem(item)
        }

        override fun onDownloadError(item: VideoTaskItem) {
            updateVideoTaskItem(item)
        }

        override fun onDownloadSuccess(item: VideoTaskItem) {
            updateVideoTaskItem(item)
        }
    }

    private fun updateVideoTaskItem(item: VideoTaskItem) {
        lifecycleScope.launch {
            adapter.updateTaskItem(item)
        }
    }

    private val mDownloadInfosCallback = IDownloadInfosCallback { items: List<VideoTaskItem> ->
        val downloadingTaskItems = mutableListOf<VideoTaskItem>()
        for (item in items) {
            if (!item.isSuccessState) downloadingTaskItems.add(item)
        }
        lifecycleScope.launch {
            adapter.setData(downloadingTaskItems)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        BaseApplication.removeListener(mListener)
        VideoDownloadManager.getInstance().fetchDownloadItems()
        VideoDownloadManager.getInstance().removeDownloadInfosCallback(mDownloadInfosCallback)
    }
}