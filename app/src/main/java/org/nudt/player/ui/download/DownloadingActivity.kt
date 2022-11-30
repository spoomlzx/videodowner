package org.nudt.player.ui.download

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import org.nudt.player.R
import org.nudt.player.databinding.ActivityDownloadingBinding

class DownloadingActivity : AppCompatActivity() {
    private val binding by lazy { ActivityDownloadingBinding.inflate(layoutInflater) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.tbCommon.tvTitle.text = getString(R.string.main_menu_offline_downloading)
        binding.tbCommon.ivBack.setOnClickListener { finish() }

        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.rvVideoDownloading.layoutManager = LinearLayoutManager(this@DownloadingActivity)
//        adapter = DownloadingAdapter(this@DownloadingActivity)
//        binding.rvVideoDownloading.adapter = adapter

        // 关闭recyclerview更新动画，防止图片闪烁
        (binding.rvVideoDownloading.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

    }

    override fun onDestroy() {
        super.onDestroy()
    }
}