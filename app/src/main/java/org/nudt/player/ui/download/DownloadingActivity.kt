package org.nudt.player.ui.download

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import org.nudt.player.R
import org.nudt.player.adapter.VideoDownloadingAdapter
import org.nudt.player.databinding.ActivityDownloadingBinding
import zlc.season.downloadx.DownloadXManager

class DownloadingActivity : AppCompatActivity() {
    private val binding by lazy { ActivityDownloadingBinding.inflate(layoutInflater) }
    private lateinit var downloadingAdapter: VideoDownloadingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.tbCommon.tvTitle.text = getString(R.string.main_menu_offline_downloading)
        binding.tbCommon.ivBack.setOnClickListener { finish() }

        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.rvVideoDownloading.layoutManager = LinearLayoutManager(this@DownloadingActivity)
        downloadingAdapter = VideoDownloadingAdapter(this@DownloadingActivity)
        binding.rvVideoDownloading.adapter = downloadingAdapter

        // 关闭recyclerview更新动画，防止图片闪烁
        (binding.rvVideoDownloading.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        // DB查询的Flow，转化为liveData可以自动隐藏已完成的任务
        DownloadXManager.queryUnfinishedTaskInfoFlow().asLiveData().observe(this) {
            downloadingAdapter.updateTaskInfoList(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}