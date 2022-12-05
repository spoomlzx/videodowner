package org.nudt.player.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.lxj.xpopup.XPopup
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.nudt.common.SLog
import org.nudt.player.R
import org.nudt.player.data.model.PlayHistory
import org.nudt.player.data.model.VideoCacheExtra
import org.nudt.player.databinding.DownloadingListItemVideoBinding
import org.nudt.player.ui.VideoViewModel
import org.nudt.player.ui.player.OnlinePlayerActivity
import org.nudt.player.utils.VideoUtil
import zlc.season.downloadx.DownloadXManager
import zlc.season.downloadx.State
import zlc.season.downloadx.database.TaskInfo
import zlc.season.downloadx.utils.ratio
import java.util.concurrent.ConcurrentHashMap

class VideoDownloadingAdapter(private val context: Context) :
    RecyclerView.Adapter<VideoDownloadingAdapter.VideoDownloadingViewHolder>() {

    private var downloadingTaskInfoList: ArrayList<TaskInfo> = arrayListOf()
    private val gson = Gson()

    private val positionMap = ConcurrentHashMap<String, Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoDownloadingViewHolder {
        return VideoDownloadingViewHolder(DownloadingListItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VideoDownloadingViewHolder, position: Int) {
        val taskInfo = downloadingTaskInfoList[position]
        val extra = gson.fromJson(taskInfo.extra, VideoCacheExtra::class.java)
        if (extra is VideoCacheExtra) {
            Glide.with(context).load(extra.vod_thumb).placeholder(R.drawable.default_image).into(holder.binding.ivVideoPic)
            holder.binding.tvTitle.text = extra.vod_name
            holder.binding.tvIndex.text = extra.vod_index
        }

        when (taskInfo.status) {
            0 -> {
                holder.binding.tvState.text = "下载"
            }
            1 -> {
                holder.binding.tvState.text = "等待中"
            }
            2 -> {
                holder.binding.tvState.text = (taskInfo.downloaded_bytes ratio taskInfo.total_bytes).toString() + "%"
                holder.binding.progressBar.progress = (taskInfo.downloaded_bytes ratio taskInfo.total_bytes).toInt()
            }
            3 -> {
                holder.binding.tvState.text = "已暂停"
            }
            4 -> {
                holder.binding.tvState.text = "下载完成"
                holder.binding.progressBar.progress = 100
            }
            5 -> {
                holder.binding.tvState.text = "下载出错，请重试"
            }
        }

        holder.binding.cvVideo.setOnClickListener {
            DownloadXManager.pauseResumeDownloadTask(taskInfo)
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

    fun updateState(taskInfo: TaskInfo) {
        getPositionById(taskInfo.task_id)?.let {
            if (downloadingTaskInfoList.isNotEmpty()) {
                downloadingTaskInfoList[it] = taskInfo
                notifyItemChanged(it)
            }
        }
    }

    fun updateTaskInfoList(list: List<TaskInfo>) {
        downloadingTaskInfoList = ArrayList(list)
        for (i in list.indices) {
            positionMap[list[i].task_id] = i
        }
        SLog.d("notifyDataSetChanged: ${gson.toJson(positionMap)}")
        notifyDataSetChanged()
    }

    private fun getPositionById(id: String): Int? {
        return positionMap[id]
    }


    class VideoDownloadingViewHolder(val binding: DownloadingListItemVideoBinding) : RecyclerView.ViewHolder(binding.root)
}