package org.nudt.player.ui.mine

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.nudt.player.data.network.doSuccess
import org.nudt.player.databinding.ActivityUpdateBinding
import org.nudt.player.ui.VideoViewModel

class UpdateActivity : AppCompatActivity() {
    private val binding by lazy { ActivityUpdateBinding.inflate(layoutInflater) }
    private val videoViewModel: VideoViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.tbCommon.tvTitle.text = "更新版本"

        initCheckUpdateView()

    }


    private fun initCheckUpdateView() {
        videoViewModel.checkUpdate()

        videoViewModel.checkUpdateResult.observe(this@UpdateActivity) { resultState ->
            resultState.doSuccess { version ->
                val packageManager = this@UpdateActivity.packageManager
                val packageInfo = packageManager.getPackageInfo(this@UpdateActivity.packageName, 0)
                val versionCode = packageInfo.longVersionCode
                // 如果有新版本则显示更新按钮
                if (version.version_code > versionCode) {
                    binding.btnUpdate.visibility = View.VISIBLE
                    binding.btnUpdate.text = "开始更新(${version.version})"

                    binding.btnUpdate.setOnClickListener {
                        val intentShare = Intent(Intent.ACTION_VIEW, Uri.parse(version.download_url))
                        startActivity(intentShare)
                    }
                } else {
                    binding.btnUpdate.visibility = View.GONE
                }
                // 当前版本
                binding.tvVersion.text = "Version ${packageInfo.versionName}"

                binding.tvUpdateMsg.text = version.update_msg
            }
        }


    }
}