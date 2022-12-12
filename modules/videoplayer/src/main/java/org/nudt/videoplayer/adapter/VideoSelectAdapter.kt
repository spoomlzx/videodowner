package org.nudt.videoplayer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.nudt.videoplayer.R
import org.nudt.videoplayer.databinding.PlayerVideoListItemBinding
import org.nudt.videoplayer.model.SubVideo

class VideoSelectAdapter(private val context: Context, private val itemClickListener: (subVideo: SubVideo) -> Unit) :
    RecyclerView.Adapter<VideoSelectAdapter.VideoSelectViewHolder>() {

    private var subVideoList: ArrayList<SubVideo> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoSelectViewHolder {
        return VideoSelectViewHolder(PlayerVideoListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VideoSelectViewHolder, position: Int) {
        val subVideo = subVideoList[position]
        holder.binding.tvTitle.text = subVideo.video_name
        holder.binding.tvIndex.text = subVideo.sub_video_name
        Glide.with(context).load(subVideo.sub_video_pic).placeholder(R.drawable.default_image).into(holder.binding.ivVideoPic)
        holder.binding.clSubVideo.setOnClickListener {
            itemClickListener.invoke(subVideo)
        }
    }

    override fun getItemCount(): Int {
        return subVideoList.size
    }

    fun updateSubVideoList(list: List<SubVideo>) {
        subVideoList = ArrayList(list)
        notifyDataSetChanged()
    }

    class VideoSelectViewHolder(val binding: PlayerVideoListItemBinding) : RecyclerView.ViewHolder(binding.root)
}