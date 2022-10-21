package org.nudt.player.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.nudt.common.CommonUtil
import org.nudt.player.adapter.PlayUrlAdapter
import org.nudt.player.data.model.PlayUrl
import org.nudt.player.data.model.VodInfoModel
import org.nudt.player.databinding.FragmentVideoDetailBinding
import org.nudt.player.ui.VideoViewModel
import org.nudt.common.SLog

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

            val bottomSheetDescription: BottomSheetBehavior<*> = BottomSheetBehavior.from(binding.bottomSheetDescription)
            //设置默认先隐藏
            bottomSheetDescription.state = BottomSheetBehavior.STATE_HIDDEN
            val height = resources.displayMetrics.heightPixels - CommonUtil.dpToPxInt(requireContext(), 251f)
            bottomSheetDescription.maxHeight = height
            bottomSheetDescription.peekHeight = height
            binding.tvVodDesc.setOnClickListener {
                if (bottomSheetDescription.state == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetDescription.setState(BottomSheetBehavior.STATE_COLLAPSED)
                } else if (bottomSheetDescription.state == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetDescription.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }

            binding.tvContent.text = vod_content
            binding.tvDirector.text = "导演：$vod_director"
            binding.tvActor.text = "演员：$vod_actor"
            binding.tvYear.text = "年份：$vod_year"

            binding.ivDescriptionClose.setOnClickListener {
                bottomSheetDescription.setState(BottomSheetBehavior.STATE_HIDDEN)
            }
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


        initPlayUrlList()

        val gridLayoutManager = GridLayoutManager(context, 6)
        binding.rvVodListAll.layoutManager = gridLayoutManager
        //binding.rvVodListAll.addItemDecoration(GridItemDecoration())
        binding.rvVodListAll.adapter = adapter
        binding.tvSerialTitle.text = "选集(${playUrlList.size})"
        binding.tvVodAllTitle.text = "分集(${playUrlList.size})"
    }

    /**
     * 设置选集区块
     */
    private fun initPlayUrlList() {
        val bottomSheetPlayUrlList: BottomSheetBehavior<*> = BottomSheetBehavior.from(binding.bottomSheetAllVod)
        //设置默认先隐藏
        bottomSheetPlayUrlList.state = BottomSheetBehavior.STATE_HIDDEN
        val height = resources.displayMetrics.heightPixels - CommonUtil.dpToPxInt(requireContext(), 251f)
        bottomSheetPlayUrlList.maxHeight = height
        bottomSheetPlayUrlList.peekHeight = height
        binding.tvVodAll.setOnClickListener {
            if (bottomSheetPlayUrlList.state == BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetPlayUrlList.setState(BottomSheetBehavior.STATE_COLLAPSED)
            } else if (bottomSheetPlayUrlList.state == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetPlayUrlList.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

        binding.tvVodAllClose.setOnClickListener {
            bottomSheetPlayUrlList.setState(BottomSheetBehavior.STATE_HIDDEN)
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
}