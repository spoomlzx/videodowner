package org.nudt.player.ui.player

import android.os.Bundle
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.nudt.common.CommonUtil
import org.nudt.common.SLog
import org.nudt.player.databinding.ActivityOnlinePlayerBinding


class OnlinePlayerActivity : BasePlayerActivity() {
    private val binding by lazy { ActivityOnlinePlayerBinding.inflate(layoutInflater) }
    private val tabTitles = arrayOf("剧集", "评论")

    private val playerViewModel: PlayerViewModel by viewModel()

    private var vodId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 设置video player 的比例为 1920:1080
        val width = resources.displayMetrics.widthPixels
        val height = width * 1080 / 1920
        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(width, height)
        binding.videoPlayer.layoutParams = params
        SLog.d("width: ${CommonUtil.pxToDpInt(this, width.toFloat())}   height: ${CommonUtil.pxToDpInt(this, height.toFloat())}")

        initPlayer(binding.videoPlayer)

        vodId = intent.getIntExtra("vodId", 0)

        playerViewModel.fetchVideoInfo(vodId).observe(this) {
            player.setTitle(it.vod_name)
            player.setPlayUrl(it.playUrlList[playerViewModel.currentIndex.value!!].url)
            player.prepareAsync()
            playerViewModel.history.value?.let { history ->
                player.seekTo(history.progress_time)
            }
            //SLog.d("ready to play ${playerViewModel.progress}")
        }


        // 监听当前play index变化，切换视频
        playerViewModel.currentIndex.observe(this) {
            playerViewModel.vodInfo.value?.let { info ->
                player.onReset()
                player.setProgressCallBackSpaceMilliss(300)
                player.setPlayUrl(info.playUrlList[it].url)
                player.startPlay()
            }

        }

        // 获取当前视频播放历史
        playerViewModel.fetchProgress(vodId)

        initTabLayout()
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

    override fun onStop() {
        SLog.e("stop player")
        playerViewModel.savePlayHistory(player.duration, player.currentPosition)
        super.onStop()
    }

}