package org.nudt.player.ui.player

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import org.nudt.common.CommonUtil
import org.nudt.common.SLog
import org.nudt.player.data.model.Video
import org.nudt.player.data.model.VodInfoModel
import org.nudt.player.databinding.ActivityOnlinePlayerBinding
import org.nudt.player.utils.VideoUtil


class OnlinePlayerActivity : BasePlayerActivity() {
    private val binding by lazy { ActivityOnlinePlayerBinding.inflate(layoutInflater) }
    private val tabTitles = arrayOf("剧集", "评论")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val playerViewModel: PlayerViewModel = ViewModelProvider(this)[PlayerViewModel::class.java]
        val width = resources.displayMetrics.widthPixels
        val height = width * 1080 / 1920
        // 设置video player 的比例为 1920:1080
        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(width, height)
        binding.videoPlayer.layoutParams = params
        initPlayer(binding.videoPlayer)
        SLog.d("width: ${CommonUtil.pxToDpInt(this, width.toFloat())}   height: ${CommonUtil.pxToDpInt(this, height.toFloat())}")

        val video = intent.getParcelableExtra<Video>("video")
        if (video == null) {
            Toast.makeText(this@OnlinePlayerActivity, "视频数据错误", Toast.LENGTH_SHORT).show()
            return
        } else {
            val playUrlList = VideoUtil.convertPlayUrlList(video.vod_play_url)
            // 直接播放第一集
            player.setTitle(video.vod_name)
            player.setPlayUrl(playUrlList[0].url)
            player.prepareAsync()

            val vod = VodInfoModel(
                video.vod_id, video.type_id, video.type_pid, video.vod_name, video.vod_actor, video.vod_director, video.vod_pic, video.vod_remarks, video.vod_class,
                video.vod_content, video.vod_area, video.vod_year, video.vod_score, playUrlList
            )
            // 更新viewModel中的vidInfoModel
            playerViewModel.setVodInfo(vod)
        }

        // 监听当前play url变化，切换视频
        playerViewModel.currentPlayUrl.observe(this) {
            player.onReset()
            player.setProgressCallBackSpaceMilliss(300)
            player.setPlayUrl(it.url)
            player.startPlay()
        }

        initTabLayout(playerViewModel, video)
    }

    /**
     * 初始化详情和评论tabLayout
     */
    private fun initTabLayout(playerViewModel: PlayerViewModel, video: Video) {
        val viewPager: ViewPager2 = binding.viewPager
        viewPager.isUserInputEnabled = false
        viewPager.adapter = object : FragmentStateAdapter(this@OnlinePlayerActivity) {
            override fun getItemCount(): Int {
                return 2
            }

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> VideoDetailFragment(playerViewModel)
                    else -> CommentFragment.newInstance(playerViewModel, video)
                }
            }
        }

        TabLayoutMediator(binding.tabs, binding.viewPager, true, true) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    override fun onDestroy() {

        super.onDestroy()
    }
}