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

        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.rvVideoDownloading.layoutManager = LinearLayoutManager(this@DownloadingActivity)
        adapter = DownloadingAdapter(this@DownloadingActivity)
        binding.rvVideoDownloading.adapter = adapter

        // 关闭recyclerview更新动画，防止图片闪烁
        (binding.rvVideoDownloading.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

    }

    override fun onDestroy() {
        super.onDestroy()
    }
}