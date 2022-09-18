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
import org.nudt.player.R
import org.nudt.player.databinding.DownloadedListItemVideoBinding
import org.nudt.player.ui.player.OfflinePlayerActivity

class DownloadedAdapter(private val context: Context) : RecyclerView.Adapter<DownloadedAdapter.DownloadedViewHolder>() {
    private var taskList: MutableList<VideoTaskItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadedViewHolder {
        return DownloadedViewHolder(DownloadedListItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: DownloadedViewHolder, position: Int) {
        val video = taskList[position]
        holder.binding.tvTitle.text = video.title
        // 如果已经下载了本地视频
        val pic = if (video.coverPath != null) video.coverPath else video.coverUrl
        Glide.with(context).load(pic).placeholder(R.drawable.default_image).into(holder.binding.ivVideoPic)

        holder.binding.cvVideo.setOnClickListener {
            val intent = Intent(context, OfflinePlayerActivity::class.java)
            intent.putExtra("url", video.filePath)
            intent.putExtra("pic", video.coverPath)
            intent.putExtra("title", video.title)
            context.startActivity(intent)
        }

        holder.binding.cvVideo.setOnLongClickListener {
            val dialog = AlertDialog.Builder(context, R.style.AlertDialog).setMessage("删除视频").setPositiveButton("删除") { dialog, id ->
                VideoDownloadManager.getInstance().deleteVideoTask(video, true)
                VideoDownloadManager.getInstance().fetchDownloadItems()
            }.setNegativeButton("取消") { dialog, id ->
                // User cancelled the dialog
            }.create()
            dialog.show()
            true
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    fun setData(items: MutableList<VideoTaskItem>) {
        taskList = items
        notifyDataSetChanged()
    }

    class DownloadedViewHolder(val binding: DownloadedListItemVideoBinding) : RecyclerView.ViewHolder(binding.root)
}