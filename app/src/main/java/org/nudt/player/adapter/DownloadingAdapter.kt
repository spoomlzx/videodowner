package org.nudt.player.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jeffmony.downloader.VideoDownloadManager
import com.jeffmony.downloader.model.VideoTaskItem
import com.jeffmony.downloader.model.VideoTaskState
import org.nudt.player.R
import org.nudt.player.databinding.DownloadingListItemVideoBinding
import org.nudt.player.ui.player.OfflinePlayerActivity

class DownloadingAdapter(private val context: Context) : RecyclerView.Adapter<DownloadingAdapter.DownloadingViewHolder>() {
    private var taskList: MutableList<VideoTaskItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadingViewHolder {
        return DownloadingViewHolder(DownloadingListItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: DownloadingViewHolder, position: Int) {
        val video = taskList[position]
        holder.binding.tvTitle.text = video.title
        // 如果已经下载了本地视频
        val pic = if (video.coverPath != null) video.coverPath else video.coverUrl
        Glide.with(context).load(pic).placeholder(R.drawable.default_image).into(holder.binding.ivVideoPic)
        // 显示下载进度
        holder.binding.progressBar.progress = video.percent.toInt()
        setStateText(video, holder.binding)
        holder.binding.cvVideo.setOnClickListener {
            if (video.isInitialTask) {
                VideoDownloadManager.getInstance().startDownload(video)
            } else if (video.isRunningTask || video.isPendingTask) {
                VideoDownloadManager.getInstance().pauseDownloadTask(video.url)
            } else if (video.isInterruptTask) {
                VideoDownloadManager.getInstance().resumeDownload(video.url)
            } else if (video.isSuccessState) {
                val intent = Intent(context, OfflinePlayerActivity::class.java)
                intent.putExtra("url", video.filePath)
                intent.putExtra("pic", video.coverPath)
                intent.putExtra("title", video.title)
                context.startActivity(intent)
            }
        }

        holder.binding.cvVideo.setOnLongClickListener {
            val dialog = AlertDialog.Builder(context, R.style.AlertDialog).setMessage("删除任务").setPositiveButton("删除") { dialog, id ->
                VideoDownloadManager.getInstance().deleteVideoTask(video, true)
                VideoDownloadManager.getInstance().fetchDownloadItems()
            }.setNegativeButton("取消") { dialog, id ->
                // User cancelled the dialog
            }.create()
            dialog.show()
            true
        }
    }

    private fun setStateText(video: VideoTaskItem, binding: DownloadingListItemVideoBinding) {
        when (video.taskState) {
            VideoTaskState.START -> binding.tvState.text = "开始下载"
            VideoTaskState.PENDING -> binding.tvState.text = "排队中"
            VideoTaskState.PREPARE -> binding.tvState.text = "准备下载"
            VideoTaskState.DOWNLOADING -> binding.tvState.text = video.speedString
            // 空间不足
            VideoTaskState.ENOSPC -> binding.tvState.text = "空间不足"
            VideoTaskState.PAUSE -> binding.tvState.text = "暂停"
            // 下载出错
            VideoTaskState.ERROR -> binding.tvState.text = "下载出错"
            VideoTaskState.SUCCESS -> binding.tvState.text = "下载成功"
            VideoTaskState.DEFAULT -> binding.tvState.text = "DEFAULT"
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    fun setData(items: MutableList<VideoTaskItem>) {
        taskList = items
        notifyDataSetChanged()
    }

    fun updateTaskItem(item: VideoTaskItem) {
        for (index in taskList.indices) {
            if (taskList[index].url == item.url) {
                taskList[index] = item
                notifyItemChanged(index)
            }
        }
    }


    class DownloadingViewHolder(val binding: DownloadingListItemVideoBinding) : RecyclerView.ViewHolder(binding.root)
}