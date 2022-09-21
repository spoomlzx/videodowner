package org.nudt.player.ui.player

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import cn.jzvd.Jzvd
import com.bumptech.glide.Glide
import com.jeffmony.downloader.VideoDownloadManager
import com.jeffmony.downloader.model.VideoTaskItem
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.nudt.player.R
import org.nudt.player.component.JzvdStdAutoOrizental
import org.nudt.player.databinding.ActivityOnlinePlayerBinding
import org.nudt.player.data.model.Video
import org.nudt.player.data.model.VideoSource
import org.nudt.player.ui.VideoViewModel
import org.nudt.player.utils.CommonUtil
import org.nudt.player.utils.SpUtils


class OnlinePlayerActivity : AppCompatActivity() {

    private val videoViewModel: VideoViewModel by viewModel()
    private val binding by lazy { ActivityOnlinePlayerBinding.inflate(layoutInflater) }

    private lateinit var player: JzvdStdAutoOrizental

    // 当前的视频
    private lateinit var currentVideo: Video

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        player = binding.jzVideo

        val video = intent.getParcelableExtra<Video>("video")
        video?.apply {
            currentVideo = video
            Glide.with(this@OnlinePlayerActivity).load(SpUtils.basePicUrl + vod_pic).into(player.posterImageView)
            binding.tvDescription.text = vod_name
            binding.ivFavor.isSelected = favor

            binding.tvFileSource.text = vod_class

            binding.tvVideoContent.text = vod_content

            player.setUp(vod_play_url, vod_name)
        }

        //fetchVideo()
        initClickListener()
    }

    /**
     * 添加点赞等按钮的监听
     */
    private fun initClickListener() {
        // 点赞图标变换状态
        binding.btnLike.setOnClickListener {
            binding.ivLike.isSelected = !binding.ivLike.isSelected
        }
        // 收藏图标变换状态
        binding.btnFavor.setOnClickListener {
            val favorState = binding.ivFavor.isSelected
            binding.ivFavor.isSelected = !favorState
            videoViewModel.setFavor(!favorState, currentVideo.vod_id)
        }

        binding.btnDownload.setOnClickListener {
            if (CommonUtil.isVideoUrl(currentVideo.vod_play_url)) {
                // Use the Builder class for convenient dialog construction
                val dialog = AlertDialog.Builder(this@OnlinePlayerActivity, R.style.AlertDialog)
                    .setMessage("下载视频")
                    .setPositiveButton("开始下载") { dialog, id ->
                        val downloadItem = VideoTaskItem(currentVideo.vod_play_url, currentVideo.vod_pic, currentVideo.vod_name, "group-1")
                        VideoDownloadManager.getInstance().startDownload(downloadItem)
                        VideoDownloadManager.getInstance().fetchDownloadItems();
                        //todo 添加提示框，显示开始下载视频，点击导航到下载页面
                    }
                    .setNegativeButton("取消") { dialog, id ->
                        // User cancelled the dialog
                    }
                    .create()
                dialog.show()
            } else {
                Toast.makeText(this@OnlinePlayerActivity, "视频地址还未加载", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        if (Jzvd.backPress()) {
            return
        }
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        Jzvd.goOnPlayOnPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        Jzvd.releaseAllVideos()
    }
}