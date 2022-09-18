package org.nudt.player.ui.player

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.jzvd.Jzvd
import com.bumptech.glide.Glide
import org.nudt.player.component.JzvdStdAutoOrizental
import org.nudt.player.databinding.ActivityOfflinePlayerBinding
import org.nudt.videoplayer.AGVideo

class OfflinePlayerActivity : AppCompatActivity() {
    private val binding by lazy { ActivityOfflinePlayerBinding.inflate(layoutInflater) }

    private lateinit var player: AGVideo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        player = binding.jzVideo

        val url = intent.getStringExtra("url")
        val pic = intent.getStringExtra("pic")
        val title = intent.getStringExtra("title")

        Glide.with(this@OfflinePlayerActivity).load(pic).into(player.posterImageView)
        player.setUp(url, title)
    }

    override fun onBackPressed() {
        if (Jzvd.backPress()) {
            return
        }
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        Jzvd.goOnPlayOnPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        Jzvd.releaseAllVideos()
    }
}