package org.nudt.player.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.lxj.xpopup.XPopup
import org.nudt.common.SLog
import org.nudt.common.formatFileSize
import org.nudt.player.R
import org.nudt.player.data.model.VideoCacheExtra
import org.nudt.player.databinding.DownloadedListItemVideoBinding
import org.nudt.player.ui.player.OfflinePlayerActivity
import zlc.season.downloadx.DownloadXManager
import zlc.season.downloadx.database.TaskInfo
import java.util.concurrent.ConcurrentHashMap

class VideoDownloadedAdapter(private val context: Context) :
    RecyclerView.Adapter<VideoDownloadedAdapter.VideoDownloadedViewHolder>() {

    private var downloadedTaskInfoList: ArrayList<TaskInfo> = arrayListOf()
    private val gson = Gson()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoDownloadedViewHolder {
        return VideoDownloadedViewHolder(DownloadedListItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VideoDownloadedViewHolder, position: Int) {
        val taskInfo = downloadedTaskInfoList[position]
        val extra = gson.fromJson(taskInfo.extra, VideoCacheExtra::class.java)
        if (extra is VideoCacheExtra) {
            Glide.with(context).load(extra.vod_thumb).placeholder(R.drawable.default_image).into(holder.binding.ivVideoPic)
            holder.binding.tvTitle.text = extra.vod_name
            holder.binding.tvIndex.text = extra.vod_index
        }

        holder.binding.tvVideoSize.text = taskInfo.total_bytes.formatFileSize()

        holder.binding.cvVideo.setOnClickListener {
            val intent = Intent(context, OfflinePlayerActivity::class.java)
            intent.putExtra("url", "${taskInfo.file_path}/${taskInfo.file_name}")
            intent.putExtra("title", "${extra.vod_name}-${extra.vod_index}")
            context.startActivity(intent)
        }

        holder.binding.cvVideo.setOnLongClickListener {
            XPopup.Builder(context).asConfirm("提示", "确认删除本视频？") {
                DownloadXManager.removeDownloadTask(taskInfo)
            }.show()
            true
        }
    }

    override fun getItemCount(): Int {
        return downloadedTaskInfoList.size
    }

    fun updateTaskInfoList(list: List<TaskInfo>) {
        downloadedTaskInfoList = ArrayList(list)
        notifyDataSetChanged()
    }

    class VideoDownloadedViewHolder(val binding: DownloadedListItemVideoBinding) : RecyclerView.ViewHolder(binding.root)
}