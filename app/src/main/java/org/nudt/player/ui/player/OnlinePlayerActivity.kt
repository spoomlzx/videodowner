package org.nudt.player.ui.player

import android.os.Bundle
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.nudt.common.SLog
import org.nudt.player.data.model.PlayHistory
import org.nudt.player.databinding.ActivityOnlinePlayerBinding
import org.nudt.videoplayer.VideoPlayer


class OnlinePlayerActivity : BasePlayerActivity() {
    private val binding by lazy { ActivityOnlinePlayerBinding.inflate(layoutInflater) }
    private val tabTitles = arrayOf("剧集", "评论")

    private val playerViewModel: PlayerViewModel by viewModel()

    private var vodId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setVideoPlayerSize()

        initPlayer(binding.videoPlayer)

        fetchVideoInfo()

        initTabLayout()
    }

    /**
     * 设置video player 的比例为 1920:1080
     */
    private fun setVideoPlayerSize() {
        val width = resources.displayMetrics.widthPixels
        val height = width * 1080 / 1920
        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(width, height)
        binding.videoPlayer.layoutParams = params
    }

    /**
     * 获取视频信息并播放
     */
    private fun fetchVideoInfo() {
        vodId = intent.getIntExtra("vodId", 0)


        player.setIndexChangeListener(object : VideoPlayer.OnIndexChangeListener {
            override fun onIndexChange(index: Int) {
                playerViewModel.setCurrent(index)
            }
        })

        playerViewModel.fetchVideoInfo(vodId).observe(this) {
            player.setSubVideoList(it.subVideoList)
            if (it.history != null) {
                player.setHistory(it.history!!.vod_index, it.history!!.progress_time)
            }
        }

        // 监听当前play index变化，切换视频
        playerViewModel.currentIndex.observe(this) {
            player.setIndex(it)
        }
    }

    /**
     * 初始化详情和评论tabLayout
     */
    private fun initTabLayout() {
        val viewPager: ViewPager2 = binding.viewPager
        viewPager.isUserInputEnabled = false
        viewPager.adapter = object : FragmentStateAdapter(this@OnlinePlayerActivity) {
            override fun getItemCount(): Int {
                return 2
            }

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> VideoDetailFragment()
                    else -> CommentFragment.newInstance(vodId)
                }
            }
        }

        TabLayoutMediator(binding.tabs, binding.viewPager, true, true) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    /**
     * 关闭播放器时同步保存视频播放记录
     */
    override fun onDestroy() {
        SLog.e("destroy player")
        playerViewModel.vodInfo.value?.apply {
            val history = PlayHistory(
                vod_id = vod_id,
                vod_name = vod_name,
                vod_pic = vod_pic,
                vod_pic_thumb = vod_pic_thumb,
                vod_pic_slide = vod_pic_slide,
                vod_remarks = vod_remarks,
                vod_index = playerViewModel.currentIndex.value ?: 0,
                total_video_num = subVideoList.size,
                progress_time = player.currentPosition,
                vod_duration = player.duration,
                last_play_time = System.currentTimeMillis()
            )
            playerViewModel.savePlayHistory(history)
        }
        super.onDestroy()
    }

}