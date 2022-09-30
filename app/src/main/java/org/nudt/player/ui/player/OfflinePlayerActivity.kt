package org.nudt.player.ui.player

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.nudt.player.databinding.ActivityOfflinePlayerBinding
import org.nudt.videoplayer.VideoPlayer

class OfflinePlayerActivity : AppCompatActivity() {
    private val binding by lazy { ActivityOfflinePlayerBinding.inflate(layoutInflater) }

    private lateinit var player: VideoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        player = binding.videoPlayer

        val url = intent.getStringExtra("url")
        val pic = intent.getStringExtra("pic")
        val title = intent.getStringExtra("title")
    }

//    override fun onBackPressed() {
//        if (Jzvd.backPress()) {
//            return
//        }
//        super.onBackPressed()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        Jzvd.goOnPlayOnPause()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        Jzvd.releaseAllVideos()
//    }
}