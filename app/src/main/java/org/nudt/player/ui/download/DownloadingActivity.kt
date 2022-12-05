package org.nudt.player.ui.download

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.gson.Gson
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.nudt.common.SLog
import org.nudt.player.R
import org.nudt.player.adapter.VideoDownloadingAdapter
import org.nudt.player.databinding.ActivityDownloadingBinding
import org.nudt.player.ui.VideoViewModel
import zlc.season.downloadx.DownloadXManager
import zlc.season.downloadx.State
import zlc.season.downloadx.database.*

class DownloadingActivity : AppCompatActivity() {
    private val binding by lazy { ActivityDownloadingBinding.inflate(layoutInflater) }
    private lateinit var downloadingAdapter: VideoDownloadingAdapter
    private val gson by lazy { Gson() }

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

        val taskInfoList = DownloadXManager.queryUnfinishedTaskInfo()
        for (taskInfo in taskInfoList) {
            val downloadTask = DownloadXManager.download(taskInfo.url, taskInfo.file_name, taskInfo.extra)
            downloadTask.state().onEach {
                when (it) {
                    is State.None -> {
                        taskInfo.status = STATUS_NONE
                    }
                    is State.Waiting -> {
                        taskInfo.status = STATUS_WAITING
                    }
                    is State.Downloading -> {
                        taskInfo.status = STATUS_DOWNLOADING
                        taskInfo.downloaded_bytes = it.progress.downloadSize
                        taskInfo.total_bytes = it.progress.totalSize
                    }
                    is State.Failed -> {
                        taskInfo.status = STATUS_FAILED
                    }
                    is State.Paused -> {
                        taskInfo.status = STATUS_PAUSED
                    }
                    is State.Succeed -> {
                        taskInfo.status = STATUS_SUCCEED
                    }
                }
                downloadingAdapter.updateState(taskInfo)
            }.launchIn(lifecycleScope)
        }

        // DB查询的Flow，转化为liveData可以自动隐藏已完成的任务
        DownloadXManager.queryUnfinishedTaskInfoFlow().asLiveData().observe(this) {
            downloadingAdapter.updateTaskInfoList(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}