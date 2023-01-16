package org.nudt.player.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.lxj.xpopup.XPopup
import org.nudt.common.formatFileSize
import org.nudt.player.R
import org.nudt.player.databinding.DownloadedListItemVideoBinding
import org.nudt.player.ui.player.OfflinePlayerActivity
import zlc.season.downloadx.database.TaskInfo

class VideoDownloadedAdapter(private val context: Context) :
    RecyclerView.Adapter<VideoDownloadedAdapter.VideoDownloadedViewHolder>() {

    private var downloadedTaskInfoList: ArrayList<TaskInfo> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoDownloadedViewHolder {
        return VideoDownloadedViewHolder(DownloadedListItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VideoDownloadedViewHolder, position: Int) {
        val taskInfo = downloadedTaskInfoList[position]
        holder.binding.ivVideoPic.load(taskInfo.video_thumb) {
            placeholder(R.drawable.default_pic)
        }
        holder.binding.tvTitle.text = taskInfo.video_name
        holder.binding.tvIndex.text = taskInfo.sub_name

        holder.binding.tvVideoSize.text = taskInfo.total_bytes.formatFileSize()

        holder.binding.cvVideo.setOnClickListener {
            val intent = Intent(context, OfflinePlayerActivity::class.java)
            intent.putExtra("url", "${taskInfo.file_path}/${taskInfo.file_name}")
            intent.putExtra("title", "${taskInfo.video_name}-${taskInfo.sub_name}")
            intent.putExtra("pic", taskInfo.video_thumb)
            context.startActivity(intent)
        }

        holder.binding.cvVideo.setOnLongClickListener {
            XPopup.Builder(context).asConfirm("提示", "确认删除本视频？") {
                // DownloadXManager.removeDownloadTask(taskInfo)
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