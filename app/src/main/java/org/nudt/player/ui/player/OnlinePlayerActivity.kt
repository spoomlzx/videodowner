package org.nudt.player.ui.player

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.nudt.player.data.model.Video
import org.nudt.player.data.model.VodInfoModel
import org.nudt.player.databinding.ActivityOnlinePlayerBinding
import org.nudt.player.ui.VideoViewModel
import org.nudt.player.utils.CommonUtil
import org.nudt.player.utils.SLog


class OnlinePlayerActivity : BasePlayerActivity() {

    private val videoViewModel: VideoViewModel by viewModel()
    private val binding by lazy { ActivityOnlinePlayerBinding.inflate(layoutInflater) }

    private val tabTitles = arrayOf("剧集", "评论")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        initPlayer(binding.videoPlayer)

        val video = intent.getParcelableExtra<Video>("video")
        if (video == null) {
            Toast.makeText(this@OnlinePlayerActivity, "视频数据错误", Toast.LENGTH_SHORT).show()
            return
        } else {
            val playUrlList = CommonUtil.convertPlayUrlList(video.vod_play_url)
            // 直接播放第一集
            player.setTitle(video.vod_name)
            player.setDataSource(playUrlList[0].url)
            player.prepareAsync()

            val vod = VodInfoModel(
                video.vod_id, video.type_id, video.type_pid, video.vod_name, video.vod_actor, video.vod_director, video.vod_pic, video.vod_remarks, video.vod_class,
                video.vod_content, video.vod_area, video.vod_year, video.vod_score, playUrlList
            )
            // 更新viewModel中的vidInfoModel
            videoViewModel.setVodInfo(vod)

            videoViewModel.setFavor(video.favor)
        }

        // 监听当前play url变化，切换视频
        videoViewModel.currentPlayUrl.observe(this) {
            player.onReset()
            player.setProgressCallBackSpaceMilliss(300)
            player.setDataSource(it.url)
            player.startPlay()
        }

        val viewPager: ViewPager2 = binding.viewPager
        viewPager.isUserInputEnabled = false
        viewPager.adapter = object : FragmentStateAdapter(this@OnlinePlayerActivity) {
            override fun getItemCount(): Int {
                return 2
            }

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> VideoDetailFragment(videoViewModel)
                    else -> CommentFragment.newInstance(videoViewModel, video.vod_name, video.vod_content)
                }
            }
        }

        TabLayoutMediator(binding.tabs, binding.viewPager, true, true) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }
}