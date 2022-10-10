package org.nudt.player.ui.player

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jeffmony.downloader.VideoDownloadManager
import com.jeffmony.downloader.model.VideoTaskItem
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.nudt.player.R
import org.nudt.player.adapter.PlayUrlAdapter
import org.nudt.player.data.model.PlayUrl
import org.nudt.player.data.model.Video
import org.nudt.player.data.model.VodInfoModel
import org.nudt.player.databinding.FragmentVideoDetailBinding
import org.nudt.player.ui.VideoViewModel
import org.nudt.player.utils.CommonUtil
import org.nudt.player.utils.SLog

class VideoDetailFragment(val viewModel: VideoViewModel) : Fragment() {

    private val binding by lazy { FragmentVideoDetailBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClickListener()
        // 监听viewModel中的vidInfoModel，更新详情信息
        viewModel.vodInfo.observe(viewLifecycleOwner) {
            SLog.d("vodInfo: in detail " + it.vod_name)
            initVideoDetail(it)
            initPlayUrlList(it.playUrlList)
        }

        viewModel.getFavor().observe(viewLifecycleOwner) {
            binding.btnFavor.isSelected = it
        }
    }

    /**
     * 加载播放器页面信息
     */
    private fun initVideoDetail(vodInfoModel: VodInfoModel) {
        vodInfoModel.apply {
            //Glide.with(this@OnlinePlayerActivity).load(SpUtils.basePicUrl + vod_pic).into(player.posterImageView)
            binding.tvVodName.text = vod_name
            binding.tvVodScore.text = vod_score + "分"
            binding.tvRemarks.text = "$vod_remarks  |  $vod_year  |  $vod_area"
            //binding.tvVideoContent.text = vod_content
        }
    }

    /**
     * init play url list view
     */
    private fun initPlayUrlList(playUrlList: ArrayList<PlayUrl>) {
        // use horizontal layout
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.rvVodList.layoutManager = linearLayoutManager
        val adapter = PlayUrlAdapter(viewModel)
        adapter.setPlayUrlList(playUrlList)

        binding.rvVodList.adapter = adapter

        binding.tvVodAll.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogBg)
            bottomSheetDialog.setContentView(R.layout.dialog_play_url_all)

            // videoPlayer->210dp,tabLayout->40dp,divider->1dp
            bottomSheetDialog.behavior.peekHeight = resources.displayMetrics.heightPixels - CommonUtil.dpToPxInt(requireContext(), 251f);
            bottomSheetDialog.behavior.isDraggable = false
            bottomSheetDialog.setCanceledOnTouchOutside(false)
            bottomSheetDialog.show()
        }


    }

    /**
     * 添加点赞等按钮的监听
     */
    private fun initClickListener() {
        // 点赞图标变换状态
        binding.btnLike.setOnClickListener {
            binding.btnLike.isSelected = !binding.btnLike.isSelected
        }
        // 收藏图标变换状态
        binding.btnFavor.setOnClickListener {
            viewModel.changeFavor()
        }

        //todo 对应多地址的下载进行修改
//        binding.btnDownload.setOnClickListener {
//            if (CommonUtil.isVideoUrl(video.vod_play_url)) {
//                // Use the Builder class for convenient dialog construction
//                val dialog = AlertDialog.Builder(requireContext(), R.style.AlertDialog)
//                    .setMessage("下载视频")
//                    .setPositiveButton("开始下载") { dialog, id ->
//                        val downloadItem = VideoTaskItem(video.vod_play_url, video.vod_pic, video.vod_name, "group-1")
//                        VideoDownloadManager.getInstance().startDownload(downloadItem)
//                        VideoDownloadManager.getInstance().fetchDownloadItems();
//                        //todo 添加提示框，显示开始下载视频，点击导航到下载页面
//                    }
//                    .setNegativeButton("取消") { dialog, id ->
//                        // User cancelled the dialog
//                    }
//                    .create()
//                dialog.show()
//            } else {
//                Toast.makeText(requireContext(), "视频地址还未加载", Toast.LENGTH_SHORT).show()
//            }
//        }
    }

    abstract class OnDetailListener {
        /**
         * 切换视频
         */
        open fun switchPlayUrl(url: String) {}

        /**
         * 播放指定视频
         */
        open fun playVideo(title: String, url: String) {}
    }
}