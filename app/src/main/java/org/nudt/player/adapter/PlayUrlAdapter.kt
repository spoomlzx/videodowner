package org.nudt.player.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.nudt.player.data.model.PlayUrl
import org.nudt.player.databinding.PlayUrlListItemBinding
import org.nudt.player.utils.SLog

class PlayUrlAdapter : RecyclerView.Adapter<PlayUrlAdapter.PlayUrlViewHolder>() {

    private var playUrlList: List<PlayUrl> = ArrayList<PlayUrl>()

    private var currentPosition: Int = 0
    private var playUrlClickListener: OnPlayUrlClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayUrlViewHolder {
        return PlayUrlViewHolder(PlayUrlListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: PlayUrlViewHolder, position: Int) {
        val playUrl = playUrlList[position]
        // 显示第几集
        holder.binding.playUrlIndex.text = (position + 1).toString()
        // 当前选中项
        val isCurrent = currentPosition == position
        holder.binding.playUrlIndex.isSelected = isCurrent
        holder.binding.hamAnimaImg.visibility = if (isCurrent) {
            View.VISIBLE
        } else {
            View.GONE
        }

        holder.binding.playUrlIndex.setOnClickListener {
            currentPosition = holder.bindingAdapterPosition
            SLog.d("current index: $currentPosition && play url: $playUrl")
            notifyDataSetChanged();
            playUrlClickListener?.onUrlClicked(playUrl.url)
        }
    }

    override fun getItemCount(): Int {
        return playUrlList.size
    }

    fun setPlayUrlList(urlList: List<PlayUrl>) {
        this.playUrlList = urlList
        notifyDataSetChanged()
    }

    fun setPlayUrlClickListener(playUrlClickListener: OnPlayUrlClickListener) {
        this.playUrlClickListener = playUrlClickListener
    }


    class PlayUrlViewHolder(val binding: PlayUrlListItemBinding) : RecyclerView.ViewHolder(binding.root)

    abstract class OnPlayUrlClickListener {
        open fun onUrlClicked(url: String) {}
    }
}