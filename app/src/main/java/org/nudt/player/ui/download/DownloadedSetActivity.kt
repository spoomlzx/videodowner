package org.nudt.player.ui.download

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import org.nudt.player.R
import org.nudt.player.adapter.VideoDownloadedSetAdapter
import org.nudt.player.data.model.VideoSet
import org.nudt.player.databinding.ActivityDownloadedSetBinding

class DownloadedSetActivity : AppCompatActivity() {
    private val binding by lazy { ActivityDownloadedSetBinding.inflate(layoutInflater) }
    private lateinit var adapter: VideoDownloadedSetAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val videos = intent.getStringExtra("videos")
        val gson = Gson()
        val videoSet = gson.fromJson(videos, VideoSet::class.java)

        binding.tbCommon.tvTitle.text = "${videoSet.videoName}(${videoSet.subVideoList.size}集)"
        binding.tbCommon.ivBack.setOnClickListener { finish() }

        binding.rvVideoDownloaded.layoutManager = LinearLayoutManager(this@DownloadedSetActivity)
        adapter = VideoDownloadedSetAdapter(this@DownloadedSetActivity)
        binding.rvVideoDownloaded.adapter = adapter

        adapter.updateTaskInfoList(videoSet.subVideoList)
    }

}