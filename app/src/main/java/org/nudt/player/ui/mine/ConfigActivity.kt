package org.nudt.player.ui.mine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import org.nudt.common.SLog
import org.nudt.player.databinding.ActivityConfigBinding
import zlc.season.downloadx.State
import zlc.season.downloadx.core.DownloadTask
import zlc.season.downloadx.download

class ConfigActivity : AppCompatActivity() {
    private val binding by lazy { ActivityConfigBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        var downloadTask: DownloadTask? = null

        downloadTask = lifecycleScope.download("http://192.168.3.4/01.mp4")
        downloadTask.state().onEach {
            when (it) {
                is State.None -> {
                    binding.btnState.text = "下载"
                }
                is State.Waiting -> {
                    binding.btnState.text = "等待中"
                }
                is State.Downloading -> {
                    binding.btnState.text = it.progress.percentStr()
                }
                is State.Failed -> {
                    binding.btnState.text = "重试"
                }
                is State.Stopped -> {
                    binding.btnState.text = "继续"
                }
                is State.Succeed -> {
                    binding.btnState.text = "安装"
                }
            }
        }.launchIn(lifecycleScope)


        binding.btnStart.setOnClickListener {
            downloadTask.let {
                when {
                    it.isStarted() -> it.stop()
                    else -> it.start()
                }
            }
        }

        binding.btnResume.setOnClickListener {
            downloadTask.config.taskManager
        }

        binding.btnRemove.setOnClickListener {
            downloadTask.remove()
        }
    }
}