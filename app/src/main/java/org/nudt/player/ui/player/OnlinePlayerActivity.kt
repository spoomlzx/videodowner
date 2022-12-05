package org.nudt.player.ui.player

import android.os.Bundle
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.nudt.common.CommonUtil
import org.nudt.common.SLog
import org.nudt.player.data.model.PlayHistory
import org.nudt.player.databinding.ActivityOnlinePlayerBinding
import org.nudt.player.ui.AppViewModel


class OnlinePlayerActivity : BasePlayerActivity() {
    private val binding by lazy { ActivityOnlinePlayerBinding.inflate(layoutInflater) }
    private val tabTitles = arrayOf("剧集", "评论")

    private val playerViewModel: PlayerViewModel by viewModel()
    private val appViewModel: AppViewModel by viewModel()

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

        playerViewModel.fetchVideoInfo(vodId).observe(this) {
            player.setTitle(it.vod_name)
            // 根据history初始化的index进行播放，
            var index = 0
            it.history?.let { history -> index = history.vod_index }
            if (it.playUrlList.size > 0) {
                player.setPlayUrl(it.playUrlList[index].url)
                player.prepareAsync()
            }

            it.history?.let { history -> player.seekTo(history.progress_time) }
            //SLog.d("ready to play ${playerViewModel.progress}")
        }

        // 监听当前play index变化，切换视频
        playerViewModel.currentIndex.observe(this) {
            // 获取到视频信息进行播放以后的切换选集操作
            playerViewModel.vodInfo.value?.let { info ->
                player.onReset()
                player.setProgressCallBackSpaceMilliss(300)
                player.setPlayUrl(info.playUrlList[it].url)
                player.startPlay()
            }
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
                total_video_num = playUrlList.size,
                progress_time = player.currentPosition,
                total_duration = player.duration,
                last_play_time = System.currentTimeMillis()
            )
            appViewModel.savePlayHistory(history)
        }
        super.onDestroy()
    }

}