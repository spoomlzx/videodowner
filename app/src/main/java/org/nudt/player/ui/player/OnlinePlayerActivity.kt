package org.nudt.player.ui.player

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.nudt.player.data.model.Video
import org.nudt.player.databinding.ActivityOnlinePlayerBinding
import org.nudt.player.ui.VideoViewModel


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
        }

        val viewPager: ViewPager2 = binding.viewPager
        viewPager.isUserInputEnabled = false
        viewPager.adapter = object : FragmentStateAdapter(this@OnlinePlayerActivity) {
            override fun getItemCount(): Int {
                return 2
            }

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> VideoDetailFragment(video, object : VideoDetailFragment.OnDetailListener() {
                        override fun switchPlayUrl(url: String) {
                            player.onReset()
                            player.setProgressCallBackSpaceMilliss(300)
                            player.setDataSource(url)
                            player.startPlay()
                        }

                        override fun playVideo(title: String, url: String) {
                            player.setTitle(title)
                            player.setDataSource(url)
                            player.prepareAsync()
                        }
                    })
                    else -> CommentFragment.newInstance(video.vod_id, video.vod_content)
                }
            }
        }

        TabLayoutMediator(binding.tabs, binding.viewPager, true, true) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }
}