package org.nudt.player.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.lxj.xpopup.XPopup
import org.nudt.player.R
import org.nudt.player.data.model.VideoSet
import org.nudt.player.databinding.DownloadedListItemVideoBinding
import org.nudt.player.ui.player.OfflinePlayerActivity
import zlc.season.downloadx.utils.formatSize

class VideoDownloadedAdapter(private val context: Context) :
    RecyclerView.Adapter<VideoDownloadedAdapter.VideoDownloadedViewHolder>() {

    private var downloadedTaskInfoList: ArrayList<VideoSet> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoDownloadedViewHolder {
        return VideoDownloadedViewHolder(DownloadedListItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VideoDownloadedViewHolder, position: Int) {
        val videoSet = downloadedTaskInfoList[position]
        holder.binding.ivVideoPic.load(videoSet.videoThumb) {
            placeholder(R.drawable.default_pic)
        }
        holder.binding.tvTitle.text = videoSet.videoName

        holder.binding.tvVideoSize.text = videoSet.totalBytes.formatSize()

        if (videoSet.subVideoList.size > 1) {
            holder.binding.llRemark.visibility = View.VISIBLE
            holder.binding.tvRemark.text = videoSet.subVideoList.size.toString()
        } else {
            holder.binding.llRemark.visibility = View.GONE
            holder.binding.cvVideo.setOnClickListener {
                val taskInfo = videoSet.subVideoList[0]
                val intent = Intent(context, OfflinePlayerActivity::class.java)
                intent.putExtra("url", "${taskInfo.file_path}/${taskInfo.file_name}")
                intent.putExtra("title", "${taskInfo.video_name}-${taskInfo.sub_name}")
                intent.putExtra("pic", taskInfo.video_thumb)
                context.startActivity(intent)
            }
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

    fun updateTaskInfoList(list: List<VideoSet>) {
        downloadedTaskInfoList = ArrayList(list)
        notifyDataSetChanged()
    }

    class VideoDownloadedViewHolder(val binding: DownloadedListItemVideoBinding) : RecyclerView.ViewHolder(binding.root)
}