package org.nudt.player.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.nudt.player.R
import org.nudt.player.data.model.PlayHistory
import org.nudt.player.databinding.HistoryMineListItemBinding
import org.nudt.player.ui.player.OnlinePlayerActivity
import org.nudt.player.utils.SpUtils

class MineHistoryAdapter(private val context: Context) : RecyclerView.Adapter<MineHistoryAdapter.FavorViewHolder>() {

    private var historyList: List<PlayHistory> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavorViewHolder {
        return FavorViewHolder(HistoryMineListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: FavorViewHolder, position: Int) {
        historyList.let {
            val playHistory = it[position]
            holder.binding.tvVideoName.text = playHistory.vod_name
            Glide.with(context).load(playHistory.vod_pic).placeholder(R.drawable.default_image).into(holder.binding.ivVideoPic)

            holder.binding.cvVideo.setOnClickListener {
                val intent = Intent(context, OnlinePlayerActivity::class.java)
                intent.putExtra("vodId", playHistory.vod_id)
                context.startActivity(intent)
            }

            holder.binding.cvVideo.setOnLongClickListener {
                val dialog = AlertDialog.Builder(context, R.style.AlertDialog).setMessage("取消关注").setPositiveButton("确认") { dialog, id ->
                    //videoViewModel.changeFavor()
                }.setNegativeButton("取消") { _, _ ->
                    // User cancelled the dialog
                }.create()
                dialog.show()
                true
            }
        }
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    fun updateFavoriteList(list: List<PlayHistory>) {
        historyList = list
        notifyDataSetChanged()
    }


    class FavorViewHolder(val binding: HistoryMineListItemBinding) : RecyclerView.ViewHolder(binding.root)
}