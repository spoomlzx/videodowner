package org.nudt.videoplayer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import org.nudt.videoplayer.R
import org.nudt.videoplayer.databinding.PlayerVideoListItemBinding
import org.nudt.videoplayer.model.SubVideo

class VideoSelectAdapter(private val context: Context, private val itemClickListener: (index: Int) -> Unit) :
    RecyclerView.Adapter<VideoSelectAdapter.VideoSelectViewHolder>() {

    private var subVideoList: ArrayList<SubVideo> = arrayListOf()

    private var currentIndex = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoSelectViewHolder {
        return VideoSelectViewHolder(PlayerVideoListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VideoSelectViewHolder, position: Int) {
        val subVideo = subVideoList[position]
        holder.binding.tvTitle.text = subVideo.video_name
        holder.binding.tvIndex.text = subVideo.sub_video_name

        holder.binding.ivVideoPic.load(subVideo.sub_video_pic) {
            placeholder(R.drawable.default_image)
        }

        holder.binding.tvTitle.isSelected = currentIndex == position

        holder.binding.tvIndex.isSelected = currentIndex == position

        holder.binding.clSubVideo.setOnClickListener {
            itemClickListener.invoke(holder.absoluteAdapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return subVideoList.size
    }

    fun updateCurrentIndex(index: Int) {
        currentIndex = index
        notifyDataSetChanged()
    }

    fun updateSubVideoList(list: List<SubVideo>) {
        subVideoList = ArrayList(list)
        notifyDataSetChanged()
    }

    class VideoSelectViewHolder(val binding: PlayerVideoListItemBinding) : RecyclerView.ViewHolder(binding.root)
}