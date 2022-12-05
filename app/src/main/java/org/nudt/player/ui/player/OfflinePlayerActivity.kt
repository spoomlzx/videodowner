package org.nudt.player.ui.player

import android.os.Bundle
import android.widget.LinearLayout
import org.nudt.player.databinding.ActivityOfflinePlayerBinding

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
        if (title != null) {
            player.setTitle(title)
        }
        if (url != null) {
            player.setPlayUrl(url)
            player.prepareAsync()
        }
    }
}