package org.nudt.player.ui.player

import android.os.Bundle
import android.widget.LinearLayout
import org.nudt.player.databinding.ActivityOfflinePlayerBinding
import org.nudt.videoplayer.model.SubVideo

class OfflinePlayerActivity : BasePlayerActivity() {
    private val binding by lazy { ActivityOfflinePlayerBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        // 设置video player 的比例为 1920:1080
        val width = resources.displayMetrics.widthPixels
        val height = width * 1080 / 1920
        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(width, height)
        binding.videoPlayer.layoutParams = params

        initPlayer(binding.videoPlayer)

        val url = intent.getStringExtra("url")
        val title = intent.getStringExtra("title")
        val pic = intent.getStringExtra("pic")

        if (title != null && url != null) {
            val subVideo = SubVideo(title, "HD", pic, url)
            player.setSubVideoList(arrayListOf(subVideo))
        }
    }
}