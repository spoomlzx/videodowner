package org.nudt.player.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.lxj.xpopup.XPopup
import org.nudt.player.R
import org.nudt.player.data.model.FavoriteVideo
import org.nudt.player.databinding.FavorListItemVideoBinding
import org.nudt.player.ui.VideoViewModel
import org.nudt.player.ui.player.OnlinePlayerActivity

class FavoriteAdapter(private val context: Context, private val videoViewModel: VideoViewModel) :
    RecyclerView.Adapter<FavoriteAdapter.FavorViewHolder>() {

    private var favoriteList: ArrayList<FavoriteVideo> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavorViewHolder {
        return FavorViewHolder(FavorListItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: FavorViewHolder, position: Int) {
        favoriteList.let {
            val video = it[position]
            holder.binding.tvTitle.text = video.vod_name

            holder.binding.ivVideoPic.load(video.vod_pic_thumb){
                placeholder(R.drawable.default_pic)
            }

            holder.binding.tvRemarks.text = video.vod_remarks

            holder.binding.cvVideo.setOnClickListener {
                val intent = Intent(context, OnlinePlayerActivity::class.java)
                intent.putExtra("vodId", video.vod_id)
                context.startActivity(intent)
            }

            holder.binding.cvVideo.setOnLongClickListener {
                XPopup.Builder(context).asConfirm("提示", "确定不再收藏本视频？") {
                    videoViewModel.deleteFavorites(video)
                    favoriteList.remove(video)
                    notifyItemRemoved(position)
                }.show()
                true
            }
        }
    }

    override fun getItemCount(): Int {
        return favoriteList.size
    }

    fun updateFavoriteList(list: List<FavoriteVideo>) {
        favoriteList = ArrayList(list)
        notifyDataSetChanged()
    }


    class FavorViewHolder(val binding: FavorListItemVideoBinding) : RecyclerView.ViewHolder(binding.root)
}