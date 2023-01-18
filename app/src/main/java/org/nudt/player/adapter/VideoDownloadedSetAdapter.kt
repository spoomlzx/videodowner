package org.nudt.player.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.lxj.xpopup.XPopup
import org.nudt.common.log
import org.nudt.player.R
import org.nudt.player.databinding.DownloadedListItemVideoBinding
import org.nudt.player.ui.player.OfflinePlayerActivity
import zlc.season.downloadx.database.TaskInfo
import zlc.season.downloadx.utils.formatSize

class VideoDownloadedSetAdapter(private val context: Context) :
    RecyclerView.Adapter<VideoDownloadedSetAdapter.VideoDownloadedViewHolder>() {

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

        holder.binding.tvVideoSize.text = taskInfo.total_bytes.formatSize()

        holder.binding.llRemark.visibility = View.GONE
        holder.binding.tvIndex.visibility = View.VISIBLE
        holder.binding.tvIndex.text = taskInfo.sub_name

        holder.binding.cvVideo.setOnClickListener {
            val intent = Intent(context, OfflinePlayerActivity::class.java)
            intent.putExtra("url", "${taskInfo.file_path}/${taskInfo.file_name}")
            intent.putExtra("title", "${taskInfo.video_name}-${taskInfo.sub_name}")
            intent.putExtra("pic", taskInfo.video_thumb)
            "${taskInfo.file_path}/${taskInfo.file_name}".log()
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