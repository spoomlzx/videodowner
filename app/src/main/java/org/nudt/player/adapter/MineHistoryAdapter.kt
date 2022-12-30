package org.nudt.player.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import org.nudt.player.R
import org.nudt.player.data.model.PlayHistory
import org.nudt.player.databinding.HistoryMineListItemBinding
import org.nudt.player.ui.player.OnlinePlayerActivity
import org.nudt.player.utils.VideoUtil.buildProgressText

class MineHistoryAdapter(private val context: Context) : RecyclerView.Adapter<MineHistoryAdapter.MineHistoryViewHolder>() {

    private var historyList: List<PlayHistory> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MineHistoryViewHolder {
        return MineHistoryViewHolder(HistoryMineListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MineHistoryViewHolder, position: Int) {
        val playHistory = historyList[position]
        holder.binding.tvVideoName.text = playHistory.vod_name

        val progress = buildProgressText(playHistory)
        holder.binding.tvVideoProgress.text = progress

        holder.binding.ivVideoPic.load(playHistory.vod_pic_thumb) {
            placeholder(R.drawable.default_image)
            transformations(RoundedCornersTransformation(12f))
        }

        holder.binding.cvHistoryItem.setOnClickListener {
            val intent = Intent(context, OnlinePlayerActivity::class.java)
            intent.putExtra("vodId", playHistory.vod_id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    fun updateFavoriteList(list: List<PlayHistory>) {
        historyList = list
        notifyDataSetChanged()
    }


    class MineHistoryViewHolder(val binding: HistoryMineListItemBinding) : RecyclerView.ViewHolder(binding.root)
}