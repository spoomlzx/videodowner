package org.nudt.player.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.nudt.player.R
import org.nudt.player.databinding.HomeListItemVideoBinding
import org.nudt.player.data.model.Video
import org.nudt.player.ui.player.OnlinePlayerActivity
import org.nudt.player.ui.VideoViewModel
import org.nudt.player.utils.SpUtils

class VideoPagingAdapter(private val context: Context, private val videoViewModel: VideoViewModel) :
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
        video?.let {
            holder.binding.tvTitle.text = video.vod_name
            // dontAnimate() 搭配placeholder确保还未加载时，保持页面布局，减少抖动
            Glide.with(context).load(video.vod_pic).placeholder(R.drawable.default_pic).into(holder.binding.ivVideoPic)
            holder.binding.cvVideo.setOnClickListener {
                val intent = Intent(context, OnlinePlayerActivity::class.java)
                intent.putExtra("vodId", video.vod_id)
                context.startActivity(intent)
            }

            // 客户端关闭删除功能，仅用于测试
//            holder.binding.cvVideo.setOnLongClickListener {
//                val dialog = AlertDialog.Builder(context, R.style.AlertDialog).setMessage("删除视频").setPositiveButton("删除") { dialog, id ->
//                    videoViewModel.removeVideo(video)
//                }.setNegativeButton("取消") { _, _ ->
//                    // User cancelled the dialog
//                }.create()
//                dialog.show()
//                true
//            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        val binding = HomeListItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BindingViewHolder(binding)
    }


    class BindingViewHolder(val binding: HomeListItemVideoBinding) : RecyclerView.ViewHolder(binding.root)
}