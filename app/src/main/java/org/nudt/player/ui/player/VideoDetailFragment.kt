package org.nudt.player.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.lxj.xpopup.XPopup
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.nudt.common.CommonUtil
import org.nudt.common.SLog
import org.nudt.player.adapter.PlayUrlAdapter
import org.nudt.player.data.model.VodInfoModel
import org.nudt.player.databinding.FragmentVideoDetailBinding
import org.nudt.player.ui.VideoViewModel

class VideoDetailFragment : Fragment() {

    private val binding by lazy { FragmentVideoDetailBinding.inflate(layoutInflater) }

    private val videoViewModel: VideoViewModel by viewModel()
    private val playerViewModel: PlayerViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClickListener()
        // 监听viewModel中的vidInfoModel，更新详情信息
        playerViewModel.vodInfo.observe(viewLifecycleOwner) {
            SLog.d("vodInfo: in detail " + it.vod_name)
            initVideoDetail(it)
        }

    }

    /**
     * 加载播放器页面信息
     */
    private fun initVideoDetail(vodInfoModel: VodInfoModel) {
        vodInfoModel.apply {
            binding.tvVodName.text = vod_name
            //tvVodScore.text = vod_score + "分"
            binding.tvRemarks.text = "$vod_remarks  |  $vod_year  |  $vod_area"
            //tvVideoContent.text = vod_content
        }

        val width = resources.displayMetrics.widthPixels
        val playerHeight = width * 1080 / 1920

        //根据视频窗口大小确定弹窗高度,3f是留出视频进度条的高度
        val height = resources.displayMetrics.heightPixels - playerHeight - CommonUtil.dpToPxInt(requireContext(), 3f)
        SLog.d("fragment popup height: $height")


        // 点击出现视频详情弹窗
        binding.tvVodDesc.setOnClickListener {
            XPopup.Builder(context)
                .hasShadowBg(false)
                .moveUpToKeyboard(false) //如果不加这个，评论弹窗会移动到软键盘上面
                //.isViewMode(true)
                .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
//                        .isThreeDrag(true) //是否开启三阶拖拽，如果设置enableDrag(false)则无效
                .asCustom(VideoDetailPopup(context!!).initPopup(vodInfoModel, height))
                .show();
        }

        // init play url list view
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.rvVodList.layoutManager = linearLayoutManager

        val adapter = PlayUrlAdapter(playerViewModel)
        adapter.setPlayUrlList(vodInfoModel.playUrlList)

        binding.rvVodList.adapter = adapter

        // 按当前播放history 滚动到指定集数位置
        playerViewModel.currentIndex.value?.let {
            binding.rvVodList.smoothScrollToPosition(it)
        }

        binding.tvSerialTitle.text = "选集(${vodInfoModel.playUrlList.size})"

        // 点击出现所有选集弹窗
        binding.tvVodAll.setOnClickListener {
            XPopup.Builder(context)
                .hasShadowBg(false)
                .moveUpToKeyboard(false) //如果不加这个，评论弹窗会移动到软键盘上面
                //.isViewMode(true)
                .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
//                        .isThreeDrag(true) //是否开启三阶拖拽，如果设置enableDrag(false)则无效
                .asCustom(VideoPlayUrlListPopup(context!!).initPopup(vodInfoModel, adapter, height))
                .show();
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

        }

        binding.btnDownload.setOnClickListener {
            XPopup.Builder(context).asConfirm("提示", "下载该视频？") {
                playerViewModel.cacheVideo()
            }.show()
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