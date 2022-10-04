package org.nudt.player.ui.player

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import com.jeffmony.downloader.VideoDownloadManager
import com.jeffmony.downloader.model.VideoTaskItem
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.nudt.player.R
import org.nudt.player.data.model.Video
import org.nudt.player.databinding.ActivityOnlinePlayerBinding
import org.nudt.player.ui.VideoViewModel
import org.nudt.player.utils.CommonUtil
import org.nudt.player.utils.SLog


class OnlinePlayerActivity : BasePlayerActivity() {

    private val videoViewModel: VideoViewModel by viewModel()
    private val binding by lazy { ActivityOnlinePlayerBinding.inflate(layoutInflater) }

    // 当前的视频
    private lateinit var currentVideo: Video

    private lateinit var playUrlList: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        initPlayer(binding.videoPlayer)

        val video = intent.getParcelableExtra<Video>("video")
        if (video == null) {
            Toast.makeText(this@OnlinePlayerActivity, "视频数据错误", Toast.LENGTH_SHORT).show()
            return
        }
        currentVideo = video

        initPlayerPage()


        //fetchVideo()
        initClickListener()
    }

    /**
     * 加载播放器页面信息
     */
    private fun initPlayerPage() {
        currentVideo.apply {
            //Glide.with(this@OnlinePlayerActivity).load(SpUtils.basePicUrl + vod_pic).into(player.posterImageView)
            binding.tvVodName.text = vod_name
            binding.tvVodScore.text = vod_score + "分"
            binding.tvRemarks.text = "$vod_remarks  |  $vod_year  |  $vod_area"
            binding.btnFavor.isSelected = favor
            //binding.tvVideoContent.text = vod_content

            val playUrlList = CommonUtil.convertPlayUrlList(vod_play_url)
            val gson = Gson()
            SLog.json(gson.toJson(playUrlList), "play url list")

            // 设置播放器
            player.setTitle(vod_name)
            //player.setDataSource("http://192.168.250.43/20220220/oqb9jc6d/index.m3u8")
            player.setDataSource(playUrlList[0].url)
            player.prepareAsync()

        }
    }

    /**
     * 添加点赞等按钮的监听
     */
    private fun initClickListener() {
        // 点赞图标变换状态
        binding.btnLike.setOnClickListener {
            binding.btnLike.isSelected = !binding.btnLike.isSelected
        }
        // 收藏图标变换状态
        binding.btnFavor.setOnClickListener {
            val favorState = binding.btnFavor.isSelected
            binding.btnFavor.isSelected = !favorState
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
}