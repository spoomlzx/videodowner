package org.nudt.player.ui.player

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.nudt.common.CommonUtil
import org.nudt.common.SLog
import org.nudt.player.data.model.Video
import org.nudt.player.data.model.VodInfoModel
import org.nudt.player.data.repository.VideoRepository
import org.nudt.player.databinding.ActivityOnlinePlayerBinding
import org.nudt.player.ui.VideoViewModel
import org.nudt.player.utils.VideoUtil


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
            player.setPlayUrl(it.playUrlList[0].url)
            player.prepareAsync()
        }

        // 监听当前play url变化，切换视频
        playerViewModel.currentPlayUrl.observe(this) {
            player.onReset()
            player.setProgressCallBackSpaceMilliss(300)
            player.setPlayUrl(it.url)
            player.startPlay()
        }

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

    override fun onDestroy() {
        playerViewModel.savePlayHistory(player.duration, player.currentPosition)
        super.onDestroy()
    }
}