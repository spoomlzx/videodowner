package org.nudt.player.ui.download

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.jeffmony.downloader.VideoDownloadManager
import com.jeffmony.downloader.listener.DownloadListener
import com.jeffmony.downloader.listener.IDownloadInfosCallback
import com.jeffmony.downloader.model.VideoTaskItem
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.coroutines.launch
import org.nudt.common.SLog
import org.nudt.player.BaseApplication
import org.nudt.player.R
import org.nudt.player.adapter.DownloadingAdapter
import org.nudt.player.databinding.ActivityDownloadingBinding
import org.nudt.player.databinding.ActivityVideoDownloadListBinding

class VideoDownloadListActivity : AppCompatActivity() {
    private val binding by lazy { ActivityVideoDownloadListBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.tbCommon.tvTitle.text = getString(R.string.main_menu_offline_download)
        binding.tbCommon.ivBack.setOnClickListener { finish() }

        requestPermission()


        // 点击进入下载列表
        binding.llDownloading.setOnClickListener {
            val intent = Intent(this@VideoDownloadListActivity, DownloadingActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
    }

    /**
     * 请求磁盘读写权限
     */
    private fun requestPermission() {
        val rxPermissions: RxPermissions = RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
            .subscribe { granted: Boolean ->
                if (granted) { // Always true pre-M
                    SLog.d("request permission success")
                } else {
                    SLog.e("request permission error")
                }
            }
    }
}