package org.nudt.player.ui.mine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.nudt.player.data.model.VideoCacheExtra
import org.nudt.player.databinding.ActivityConfigBinding
import zlc.season.downloadx.DownloadXManager
import zlc.season.downloadx.State
import zlc.season.downloadx.database.DownloadTaskManager

class ConfigActivity : AppCompatActivity() {
    private val binding by lazy { ActivityConfigBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val gson = Gson()

        val video1 = VideoCacheExtra("切尔诺贝利", "http://192.168.0.173/upload/vodthumb/xiaomaomi/26/fb8cdd7bfcf14d9fbd2847e38c8bcd4f.jpg", "高清")
        val downloadTask1 = DownloadXManager.download("http://192.168.3.4/01.mp4", "", gson.toJson(video1))
        DownloadXManager.download("http://192.168.3.4/04.mp4", "", gson.toJson(video1))
        downloadTask1.state().onEach {
            when (it) {
                is State.None -> {
                    binding.btnState1.text = "下载"
                }
                is State.Waiting -> {
                    binding.btnState1.text = "等待中"
                }
                is State.Downloading -> {
                    binding.btnState1.text = it.progress.percentStr()
                }
                is State.Failed -> {
                    binding.btnState1.text = "重试"
                }
                is State.Paused -> {
                    binding.btnState1.text = "继续"
                }
                is State.Succeed -> {
                    binding.btnState1.text = "安装"
                }
            }
        }.launchIn(lifecycleScope)


        binding.btnStart1.setOnClickListener {
            downloadTask1.let {
                when {
                    it.isStarted() -> DownloadXManager.pauseDownloadTask(it)
                    else -> DownloadXManager.startDownloadTask(it)
                }
            }
        }
        val video2 = VideoCacheExtra("百妖谱", "http://192.168.0.173/upload/vodthumb/xiaomaomi/31/98ca3369f818460bb37d4887b393e16a.jpg", "第2集")
        val downloadTask2 = DownloadXManager.download("http://192.168.3.4/02.mp4", "", gson.toJson(video2))
        downloadTask2.state().onEach {
            when (it) {
                is State.None -> {
                    binding.btnState2.text = "下载"
                }
                is State.Waiting -> {
                    binding.btnState2.text = "等待中"
                }
                is State.Downloading -> {
                    binding.btnState2.text = it.progress.percentStr()
                }
                is State.Failed -> {
                    binding.btnState2.text = "重试"
                }
                is State.Paused -> {
                    binding.btnState2.text = "继续"
                }
                is State.Succeed -> {
                    binding.btnState2.text = "安装"
                }
            }
        }.launchIn(lifecycleScope)


        binding.btnStart2.setOnClickListener {
            downloadTask2.let {
                when {
                    it.isStarted() -> DownloadXManager.pauseDownloadTask(it)
                    else -> DownloadXManager.startDownloadTask(it)
                }
            }
        }

        val video3 = VideoCacheExtra("绝命毒师第三季", "http://192.168.0.173/upload/vodthumb/xiaomaomi/28/b466b886ac50470381eeabb0c1053cd0.jpg", "第5集")
        val downloadTask3 = DownloadXManager.download("http://192.168.3.4/03.mp4", "", gson.toJson(video3))
        downloadTask3.state().onEach {
            when (it) {
                is State.None -> {
                    binding.btnState3.text = "下载"
                }
                is State.Waiting -> {
                    binding.btnState3.text = "等待中"
                }
                is State.Downloading -> {
                    binding.btnState3.text = it.progress.percentStr()
                }
                is State.Failed -> {
                    binding.btnState3.text = "重试"
                }
                is State.Paused -> {
                    binding.btnState3.text = "继续"
                }
                is State.Succeed -> {
                    binding.btnState3.text = "安装"
                }
            }
        }.launchIn(lifecycleScope)


        binding.btnStart3.setOnClickListener {
            downloadTask3.let {
                when {
                    it.isStarted() -> DownloadXManager.pauseDownloadTask(it)
                    else -> DownloadXManager.startDownloadTask(it)
                }
            }
        }

        binding.btnRemove.setOnClickListener {
            DownloadXManager.removeDownloadTask(downloadTask1.buildTaskInfo())
            DownloadXManager.removeDownloadTask(downloadTask2.buildTaskInfo())
            DownloadXManager.removeDownloadTask(downloadTask3.buildTaskInfo())
        }

        val taskManager = DownloadTaskManager(this@ConfigActivity)
        binding.btnInsert.setOnClickListener {
            val taskInfo = downloadTask1.buildTaskInfo()
            lifecycleScope.launch {
                taskManager.insertTaskInfo(taskInfo)
            }
        }

        binding.btnQuery.setOnClickListener {
            val taskInfoList = taskManager.queryUnfinishedTaskInfo()

            binding.tvInfo.text = gson.toJson(taskInfoList)
        }


        binding.btnTest.setOnClickListener {
            binding.tvInfo.text = gson.toJson(video1)
        }
    }
}