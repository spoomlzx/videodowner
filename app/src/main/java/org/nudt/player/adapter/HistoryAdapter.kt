package org.nudt.player.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lxj.xpopup.XPopup
import org.nudt.player.R
import org.nudt.player.data.model.PlayHistory
import org.nudt.player.databinding.HistoryListItemBinding
import org.nudt.player.ui.VideoViewModel
import org.nudt.player.ui.player.OnlinePlayerActivity
import org.nudt.player.ui.player.PlayerViewModel
import org.nudt.player.utils.VideoUtil

class HistoryAdapter(private val context: Context, private val videoViewModel: VideoViewModel) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private var historyList: ArrayList<PlayHistory> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder(HistoryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        historyList.let {
            val playHistory = it[position]
            holder.binding.tvVideoName.text = playHistory.vod_name

            val progress = VideoUtil.buildProgressText(playHistory)
            holder.binding.tvVideoProgress.text = progress
            val pic = if (playHistory.vod_pic_thumb?.startsWith("http") == true) playHistory.vod_pic_thumb else playHistory.vod_pic

            Glide.with(context).load(pic).placeholder(R.drawable.default_image).into(holder.binding.ivVideoPic)

            holder.binding.cvHistoryItem.setOnClickListener {
                val intent = Intent(context, OnlinePlayerActivity::class.java)
                intent.putExtra("vodId", playHistory.vod_id)
                context.startActivity(intent)
            }

            holder.binding.cvHistoryItem.setOnLongClickListener {
//                val dialog = AlertDialog.Builder(context, R.style.AlertDialog).setMessage("取消关注").setPositiveButton("确认") { dialog, id ->
//                    //videoViewModel.changeFavor()
//                }.setNegativeButton("取消") { _, _ ->
//                    // User cancelled the dialog
//                }.create()
//                dialog.show()
                XPopup.Builder(context).asConfirm("提示", "确认删除本条记录？") {
                    videoViewModel.deleteHistory(playHistory)
                    historyList.remove(playHistory)
                    notifyItemRemoved(position)
                }.show()
                true
            }
        }
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    fun updateFavoriteList(list: List<PlayHistory>) {
        historyList = ArrayList(list)
        notifyDataSetChanged()
    }


    class HistoryViewHolder(val binding: HistoryListItemBinding) : RecyclerView.ViewHolder(binding.root)
}