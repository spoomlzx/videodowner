package org.nudt.player.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import org.nudt.player.R
import org.nudt.player.data.model.Video
import org.nudt.player.databinding.HomeListItemVideoBinding
import org.nudt.player.databinding.HomeListItemVideoHorizontalBinding
import org.nudt.player.ui.home.VideoFragment
import org.nudt.player.ui.player.OnlinePlayerActivity
import org.nudt.player.utils.VideoUtil

class VideoPagingAdapter(private val context: Context, private val itemViewType: Int = 1) :
    PagingDataAdapter<Video, VideoPagingAdapter.BindingViewHolder>(object : DiffUtil.ItemCallback<Video>() {
        override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean {
            return oldItem.vod_id == newItem.vod_id
        }

        override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean {
            return oldItem == newItem
        }

    }) {

    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {
        val video: Video? = getItem(position)
        if (itemViewType == VideoFragment.HORIZONTAL_PIC) {
            val binding = holder.binding as HomeListItemVideoHorizontalBinding
            video?.let {
                binding.tvTitle.text = video.vod_name
                val pic = if (video.vod_pic_thumb?.isNotEmpty() == true) video.vod_pic_thumb else video.vod_pic
                Glide.with(context).load(VideoUtil.getPicUrl(pic)).placeholder(R.drawable.default_pic).into(binding.ivVideoPic)
                binding.cvVideo.setOnClickListener {
                    val intent = Intent(context, OnlinePlayerActivity::class.java)
                    intent.putExtra("vodId", video.vod_id)
                    context.startActivity(intent)
                }
            }
        } else {
            val binding = holder.binding as HomeListItemVideoBinding
            video?.let {
                binding.tvTitle.text = video.vod_name

                Glide.with(context).load(VideoUtil.getPicUrl(video.vod_pic)).placeholder(R.drawable.default_pic).into(binding.ivVideoPic)
                binding.cvVideo.setOnClickListener {
                    val intent = Intent(context, OnlinePlayerActivity::class.java)
                    intent.putExtra("vodId", video.vod_id)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        return if (itemViewType == VideoFragment.HORIZONTAL_PIC) {
            BindingViewHolder(HomeListItemVideoHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            BindingViewHolder(HomeListItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return itemViewType
    }

    class BindingViewHolder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)
}