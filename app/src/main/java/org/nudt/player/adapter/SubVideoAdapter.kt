package org.nudt.player.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.nudt.common.log
import org.nudt.player.databinding.PlayUrlListItemBinding
import org.nudt.player.ui.player.PlayerViewModel
import org.nudt.videoplayer.model.SubVideo

class SubVideoAdapter(private val playerViewModel: PlayerViewModel) : RecyclerView.Adapter<SubVideoAdapter.SubVideoViewHolder>() {

    private var subVideoList: List<SubVideo> = arrayListOf()

    private var currentPosition: Int = playerViewModel.currentIndex.value ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubVideoViewHolder {
        return SubVideoViewHolder(PlayUrlListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: SubVideoViewHolder, position: Int) {
        val subVideo = subVideoList[position]
        // 显示第几集
        holder.binding.tvPlayUrlIndex.text = subVideo.sub_video_name
        // 当前选中项
        val isCurrent = currentPosition == position
        holder.binding.tvPlayUrlIndex.isSelected = isCurrent
        holder.binding.hamAnimaImg.visibility = if (isCurrent) View.VISIBLE else View.GONE

        holder.binding.tvPlayUrlIndex.setOnClickListener {
            currentPosition = holder.bindingAdapterPosition
            "current index: $currentPosition && play url: $subVideo".log()
            notifyDataSetChanged()
            playerViewModel.setCurrent(currentPosition)
        }
    }

    override fun getItemCount(): Int {
        return subVideoList.size
    }

    fun updateCurrent(index: Int) {
        currentPosition = index
        notifyDataSetChanged()
    }

    fun setPlayUrlList(urlList: List<SubVideo>) {
        this.subVideoList = urlList
        notifyDataSetChanged()
    }

    class SubVideoViewHolder(val binding: PlayUrlListItemBinding) : RecyclerView.ViewHolder(binding.root)
}