package org.nudt.player.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.nudt.player.R
import org.nudt.player.data.model.Video
import org.nudt.player.databinding.RecommendListItemVideoBinding
import org.nudt.player.ui.player.OnlinePlayerActivity
import org.nudt.player.utils.VideoUtil

class RecommendAdapter(private val context: Context) : RecyclerView.Adapter<RecommendAdapter.RecommendViewHolder>() {

    private var recommendVideoList: List<Video> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendViewHolder {
        return RecommendViewHolder(RecommendListItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecommendViewHolder, position: Int) {
        val video = recommendVideoList[position]
        holder.binding.tvVideoName.text = video.vod_name
        holder.binding.tvVideoScore.text = video.vod_score

        Glide.with(context).load(VideoUtil.getPicUrl(video.vod_pic_thumb)).placeholder(R.drawable.default_image).into(holder.binding.ivVideoPic)

        holder.binding.cvRecommendItem.setOnClickListener {
            val intent = Intent(context, OnlinePlayerActivity::class.java)
            intent.putExtra("vodId", video.vod_id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return recommendVideoList.size
    }

    fun updateRecommendList(list: List<Video>) {
        recommendVideoList = list
        notifyDataSetChanged()
    }


    class RecommendViewHolder(val binding: RecommendListItemVideoBinding) : RecyclerView.ViewHolder(binding.root)
}