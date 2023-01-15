package org.nudt.player.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.lxj.xpopup.XPopup
import org.nudt.player.R
import org.nudt.player.databinding.DownloadingListItemVideoBinding
import zlc.season.downloadx.DownloadXManager
import zlc.season.downloadx.State
import zlc.season.downloadx.database.STATUS_DOWNLOADING
import zlc.season.downloadx.database.TaskInfo
import zlc.season.downloadx.utils.ratio

class VideoDownloadingAdapter(private val context: Context) :
    RecyclerView.Adapter<VideoDownloadingAdapter.VideoDownloadingViewHolder>() {

    private var downloadingTaskInfoList: ArrayList<TaskInfo> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoDownloadingViewHolder {
        return VideoDownloadingViewHolder(DownloadingListItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VideoDownloadingViewHolder, position: Int) {
        val taskInfo = downloadingTaskInfoList[position]
        holder.binding.ivVideoPic.load(taskInfo.video_thumb) {
            placeholder(R.drawable.default_pic)
        }
        holder.binding.tvTitle.text = taskInfo.video_name
        holder.binding.tvIndex.text = taskInfo.sub_name

        holder.binding.progressBar.progress = (taskInfo.downloaded_bytes ratio taskInfo.total_bytes).toInt()

        val downloadTask = DownloadXManager.getDownloadTask(taskInfo.url)
        // 每次更新页面后重新设置监听
        downloadTask?.stateListener {
            when (it) {
                is State.None -> {
                    holder.binding.tvState.text = "准备下载"
                }
                is State.Waiting -> {
                    holder.binding.tvState.text = "等待中"
                }
                is State.Downloading -> {
                    taskInfo.status = STATUS_DOWNLOADING
                    taskInfo.downloaded_bytes = it.progress.downloadSize
                    taskInfo.total_bytes = it.progress.totalSize

                    holder.binding.tvState.text = it.progress.percentStr()
                    holder.binding.progressBar.progress = it.progress.percent().toInt()
                }
                is State.Failed -> {
                    holder.binding.tvState.text = "下载出错，请重试"
                }
                is State.Paused -> {
                    holder.binding.tvState.text = "已暂停"
                }
                is State.Succeed -> {
                    holder.binding.tvState.text = "下载完成"
                    holder.binding.progressBar.progress = 100
                }
            }
        }

        holder.binding.cvVideo.setOnClickListener {
            if (downloadTask != null) {
                if (downloadTask.isStarted()) {
                    //"stop task ${downloadTask.param.tag()}".log()
                    downloadTask.pause()
                } else if (downloadTask.canStart()) {
                    //"restart task ${downloadTask.param.tag()}".log()
                    downloadTask.start()
                }
            }
        }

        holder.binding.cvVideo.setOnLongClickListener {
            XPopup.Builder(context).asConfirm("提示", "确认删除本视频？") {
                DownloadXManager.removeDownloadTask(taskInfo)
            }.show()
            true
        }
    }

    override fun getItemCount(): Int {
        return downloadingTaskInfoList.size
    }

    fun updateTaskInfoList(list: List<TaskInfo>) {
        downloadingTaskInfoList = ArrayList(list)
        //SLog.d("notifyDataSetChanged: ${gson.toJson(positionMap)}")
        notifyDataSetChanged()
    }

    class VideoDownloadingViewHolder(val binding: DownloadingListItemVideoBinding) : RecyclerView.ViewHolder(binding.root)
}