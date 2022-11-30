package org.nudt.player.ui.setting

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.jiang.awesomedownloader.core.AwesomeDownloader
import com.jiang.awesomedownloader.tool.PathSelector
import org.nudt.player.R
import org.nudt.player.databinding.ActivitySettingBinding
import org.nudt.player.utils.SpUtils


class SettingActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySettingBinding.inflate(layoutInflater) }

    private val ip = SpUtils.ip

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.tbCommon.tvTitle.text = getString(R.string.main_nav_config)
        binding.tbCommon.ivBack.setOnClickListener { finish() }

        binding.tvUrl.text = ip

        binding.clConfigUrl.setOnClickListener {
            val editText = EditText(this@SettingActivity)
            editText.text.append(ip)
            val inputDialog = AlertDialog.Builder(this@SettingActivity, R.style.AlertDialog)
            inputDialog.setTitle("设置基本网址").setView(editText)
            inputDialog.setPositiveButton("确定") { dialog, which ->
                SpUtils.ip = editText.text.toString()
                binding.tvUrl.text = SpUtils.ip
            }.show()
        }

        AwesomeDownloader.initWithServiceMode(this)

        AwesomeDownloader.addOnProgressChangeListener { progress ->
            binding.tvState.text = "progress: $progress"
        }.addOnStopListener { downloadBytes, totalBytes ->
            binding.tvState.text = "stop: $downloadBytes / $totalBytes"
        }.addOnFinishedListener { filePath, fileName ->
            binding.tvState.text = "finish: $filePath / $fileName"
        }.addOnErrorListener { exception ->
            binding.tvState.text = "error: $exception"
        }

        binding.button.setOnClickListener {
            val url = "http://192.168.0.101/01.mp4"
            //获取应用私有照片储存路径
            val filePath = PathSelector(applicationContext).getDownloadsDirPath()
            binding.tvFile.text = filePath
            //加入下载队列
            AwesomeDownloader.enqueue(url, filePath, "01.mp4")
        }


    }
}