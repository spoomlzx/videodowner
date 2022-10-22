package org.nudt.player.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.nudt.player.R
import org.nudt.player.databinding.FavorListItemVideoBinding
import org.nudt.player.data.model.Video
import org.nudt.player.ui.VideoViewModel
import org.nudt.player.ui.player.OnlinePlayerActivity
import org.nudt.player.utils.SpUtils

class FavoriteAdapter(private val context: Context, private val videoViewModel: VideoViewModel) :
    RecyclerView.Adapter<FavoriteAdapter.FavorViewHolder>() {

    private var favoriteList: MutableList<Video> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavorViewHolder {
        return FavorViewHolder(FavorListItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: FavorViewHolder, position: Int) {
        favoriteList.let {
            val video = it[position]
            holder.binding.tvTitle.text = video.vod_name
            Glide.with(context).load(SpUtils.basePicUrl + video.vod_pic).placeholder(R.drawable.default_image).into(holder.binding.ivVideoPic)

            holder.binding.cvVideo.setOnClickListener {
                val intent = Intent(context, OnlinePlayerActivity::class.java)
                intent.putExtra("vodId", video.vod_id)
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
        return favoriteList.size
    }

    fun updateFavoriteList(list: MutableList<Video>) {
        favoriteList = list
        notifyDataSetChanged()
    }


    class FavorViewHolder(val binding: FavorListItemVideoBinding) : RecyclerView.ViewHolder(binding.root)
}