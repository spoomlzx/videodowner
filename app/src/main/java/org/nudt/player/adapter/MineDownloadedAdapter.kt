package org.nudt.player.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.gson.Gson
import org.nudt.common.formatFileSize
import org.nudt.player.R
import org.nudt.player.data.model.VideoCacheExtra
import org.nudt.player.databinding.DownloadedMineListItemVideoBinding
import org.nudt.player.ui.player.OfflinePlayerActivity
import zlc.season.downloadx.database.TaskInfo

class MineDownloadedAdapter(private val context: Context) : RecyclerView.Adapter<MineDownloadedAdapter.MineDownloadedViewHolder>() {

    private var downloadedTaskInfoList: ArrayList<TaskInfo> = arrayListOf()
    private val gson = Gson()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MineDownloadedViewHolder {
        return MineDownloadedViewHolder(DownloadedMineListItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MineDownloadedViewHolder, position: Int) {
        val taskInfo = downloadedTaskInfoList[position]
        val extra = gson.fromJson(taskInfo.extra, VideoCacheExtra::class.java)
        var title = "未知视频"
        if (extra is VideoCacheExtra) {
            title = "${extra.vod_name}-${extra.vod_index}"

            holder.binding.ivVideoPic.load(extra.vod_thumb) {
                placeholder(R.drawable.default_pic)
            }

        }

        holder.binding.tvVideoName.text = title

        holder.binding.tvVideoSize.text = taskInfo.total_bytes.formatFileSize()

        holder.binding.cvDownloadedItem.setOnClickListener {
            val intent = Intent(context, OfflinePlayerActivity::class.java)
            intent.putExtra("url", "${taskInfo.file_path}/${taskInfo.file_name}")
            intent.putExtra("title", title)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return downloadedTaskInfoList.size
    }

    fun updateTaskInfoList(list: List<TaskInfo>) {
        downloadedTaskInfoList = ArrayList(list)
        notifyDataSetChanged()
    }


    class MineDownloadedViewHolder(val binding: DownloadedMineListItemVideoBinding) : RecyclerView.ViewHolder(binding.root)
}