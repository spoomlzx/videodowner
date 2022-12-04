package org.nudt.player.ui.mine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.core.component.getScopeName
import org.nudt.common.SLog
import org.nudt.player.databinding.ActivityConfigBinding
import zlc.season.downloadx.Downloader
import zlc.season.downloadx.State
import zlc.season.downloadx.core.DownloadTask
import zlc.season.downloadx.database.DownloadTaskManager
import zlc.season.downloadx.database.TaskInfo
import zlc.season.downloadx.download

class ConfigActivity : AppCompatActivity() {
    private val binding by lazy { ActivityConfigBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val downloadTask = Downloader.download("http://192.168.3.4/01.mp4")
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

        binding.btnRemove.setOnClickListener {
            downloadTask.remove()
        }
        val taskManager = DownloadTaskManager(this@ConfigActivity)
        binding.btnInsert.setOnClickListener {


            val taskInfo = TaskInfo("http://192.168.3.4/01.mp4", "01.mp4", "/234/23/", "", "http://192.168.3.4/01.mp4", 500, 1000, System.currentTimeMillis(), 1)
            lifecycleScope.launch {
                taskManager.insertTaskInfo(taskInfo)
            }
        }

        binding.btnQuery.setOnClickListener {
            val taskInfo = taskManager.findTaskInfoByUrl("http://192.168.3.4/01.mp4")
            val gson = Gson()
            binding.tvInfo.text = gson.toJson(taskInfo)

        }

    }
}