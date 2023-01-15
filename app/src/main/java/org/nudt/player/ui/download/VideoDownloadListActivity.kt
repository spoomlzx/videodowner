package org.nudt.player.ui.download

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import org.nudt.player.R
import org.nudt.player.adapter.VideoDownloadedAdapter
import org.nudt.player.databinding.ActivityVideoDownloadListBinding
import zlc.season.downloadx.DownloadXManager

class VideoDownloadListActivity : AppCompatActivity() {
    private val binding by lazy { ActivityVideoDownloadListBinding.inflate(layoutInflater) }
    private lateinit var downloadedAdapter: VideoDownloadedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.tbCommon.tvTitle.text = getString(R.string.main_menu_offline_download)
        // 左上角返回按钮
        binding.tbCommon.ivBack.setOnClickListener { finish() }

        //requestPermission()


        // 点击进入下载列表
        binding.llDownloading.setOnClickListener {
            val intent = Intent(this@VideoDownloadListActivity, DownloadingActivity::class.java)
            startActivity(intent)
        }

        initDownloadingView()

        initRecyclerView()
    }

    private fun initDownloadingView() {
        DownloadXManager.queryUnfinishedTaskInfoFlow().asLiveData().observe(this) {
            if (it.isEmpty()) {
                binding.llDownloading.visibility = View.GONE
            } else {
                val taskInfo = it.first()

                binding.ivDownloadingVideoPic.load(taskInfo.video_thumb) {
                    placeholder(R.drawable.default_pic)
                }

                binding.tvDownloadingTitle.text = taskInfo.video_name
                binding.tvDownloadingIndex.text = taskInfo.sub_name
                binding.tvDownloadingVideoNum.text = "${it.size}个内容"
            }
        }
    }

    private fun initRecyclerView() {
        binding.rvVideoDownloaded.layoutManager = LinearLayoutManager(this@VideoDownloadListActivity)
        downloadedAdapter = VideoDownloadedAdapter(this@VideoDownloadListActivity)
        binding.rvVideoDownloaded.adapter = downloadedAdapter

        DownloadXManager.queryFinishedTaskInfoFlow().asLiveData().observe(this) {
            downloadedAdapter.updateTaskInfoList(it)
        }
    }
}