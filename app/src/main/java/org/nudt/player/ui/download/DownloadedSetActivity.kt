package org.nudt.player.ui.download

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.nudt.player.R
import org.nudt.player.databinding.ActivityDownloadedSetBinding

class DownloadedSetActivity : AppCompatActivity() {
    private val binding by lazy { ActivityDownloadedSetBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        binding.tbCommon.tvTitle.text = getString(R.string.main_menu_offline_downloading)
        binding.tbCommon.ivBack.setOnClickListener { finish() }

    }
}