package org.nudt.player.ui.player

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.lxj.xpopup.XPopup
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.nudt.common.CommonUtil
import org.nudt.common.log
import org.nudt.common.shortToast
import org.nudt.player.R
import org.nudt.player.adapter.RecommendAdapter
import org.nudt.player.adapter.SubVideoAdapter
import org.nudt.player.data.model.FavoriteVideo
import org.nudt.player.data.model.VodInfoModel
import org.nudt.player.data.network.doFailure
import org.nudt.player.data.network.doSuccess
import org.nudt.player.databinding.FragmentVideoDetailBinding
import org.nudt.player.utils.SpUtils

class VideoDetailFragment : Fragment() {

    private val binding by lazy { FragmentVideoDetailBinding.inflate(layoutInflater) }
    private lateinit var recommendAdapter: RecommendAdapter
    private lateinit var subVideoAdapter: SubVideoAdapter

    private val playerViewModel: PlayerViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        // 监听viewModel中的vidInfoModel，更新详情信息
        playerViewModel.vodInfo.observe(viewLifecycleOwner) {
            // "vodInfo: in detail ${it.vod_name}".log()
            it.log()
            initVideoDetail(it)

            playerViewModel.getFavoriteById(it.vod_id).observe(viewLifecycleOwner) { favoriteVideo ->
                binding.btnFavor.isSelected = favoriteVideo != null
            }
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

        initRecommend(vodInfoModel.type_id)

        val width = resources.displayMetrics.widthPixels
        val playerHeight = width * 1080 / 1920

        //根据视频窗口大小确定弹窗高度,3f是留出视频进度条的高度
        val height = resources.displayMetrics.heightPixels - playerHeight - CommonUtil.dpToPxInt(requireContext(), 3f)
        "fragment popup height: $height".log()


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

        subVideoAdapter = SubVideoAdapter(playerViewModel)
        subVideoAdapter.setPlayUrlList(vodInfoModel.subVideoList)

        binding.rvVodList.adapter = subVideoAdapter

        // 按当前播放history 滚动到指定集数位置
        playerViewModel.currentIndex.observe(viewLifecycleOwner) {
            binding.rvVodList.smoothScrollToPosition(it)
            subVideoAdapter.updateCurrent(it)
        }


        binding.tvSerialTitle.text = "选集(${vodInfoModel.subVideoList.size})"

        // 点击出现所有选集弹窗
        binding.tvVodAll.setOnClickListener {
            XPopup.Builder(context)
                .hasShadowBg(false)
                .moveUpToKeyboard(false) //如果不加这个，评论弹窗会移动到软键盘上面
                //.isViewMode(true)
                .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
//                        .isThreeDrag(true) //是否开启三阶拖拽，如果设置enableDrag(false)则无效
                .asCustom(VideoPlayUrlListPopup(context!!).initPopup(vodInfoModel, subVideoAdapter, height))
                .show();
        }
    }

    /**
     * 推荐视频
     */
    private fun initRecommend(type: Int) {
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.rvVodRecommend.layoutManager = linearLayoutManager

        context?.let {
            recommendAdapter = RecommendAdapter(it)
            binding.rvVodRecommend.adapter = recommendAdapter
        }
        playerViewModel.getVideoRecommend(type)

        playerViewModel.recommendVideoList.observe(viewLifecycleOwner) {
            it.log()
            recommendAdapter.updateRecommendList(it)
        }
    }

    /**
     * 添加点赞等按钮的监听
     */
    private fun initView() {


        // 点赞图标变换状态
        binding.btnLike.setOnClickListener {
            binding.btnLike.isSelected = !binding.btnLike.isSelected
        }

        // 收藏图标变换状态
        binding.btnFavor.setOnClickListener {
            if (binding.btnFavor.isSelected) {
                // 取消收藏
                playerViewModel.vodInfo.value?.let { it1 -> playerViewModel.deleteFavorites(it1.vod_id) }
            } else {
                // 添加收藏
                playerViewModel.vodInfo.value?.apply {
                    val favoriteVideo = FavoriteVideo(
                        vod_id = vod_id,
                        vod_name = vod_name,
                        vod_pic = vod_pic,
                        vod_pic_thumb = vod_pic_thumb,
                        vod_pic_slide = vod_pic_slide,
                        vod_remarks = vod_remarks,
                        total_video_num = playerViewModel.currentIndex.value ?: 0,
                        vod_duration = 0L,
                        add_time = System.currentTimeMillis()
                    )
                    playerViewModel.addFavorite(favoriteVideo)
                }
            }
        }

        // 单独下载一个视频
        binding.btnDownload.setOnClickListener {
            AlertDialog.Builder(context, R.style.AlertDialog).setMessage(R.string.download_this_video)
                .setPositiveButton(R.string.text_sure) { _, _ ->
                    val ret = playerViewModel.cacheVideo()
                    if (!ret) {
                        shortToast(R.string.cannot_download)
                    }
                }.setNegativeButton(R.string.text_cancel) { _, _ -> }
                .create().show()
        }

        binding.btnReportError.setOnClickListener {
            if (binding.btnReportError.isSelected) {
                shortToast(R.string.do_not_reform)
                return@setOnClickListener
            }
            var content = ""
            playerViewModel.vodInfo.value?.let {
                val subVideoName = it.subVideoList[playerViewModel.currentIndex.value!!].sub_video_name
                content = "视频：[${it.vod_name} - $subVideoName] "
            }
            XPopup.Builder(context)
                .hasStatusBarShadow(false)
                .hasNavigationBar(false) //.dismissOnBackPressed(false)
                .isDestroyOnDismiss(true) //对于只使用一次的弹窗对象，推荐设置这个
                .autoOpenSoftInput(true)
                .asInputConfirm("提交错误", content, null, "具体问题") {
                    playerViewModel.reportVideoError(SpUtils.userNickName, content + it)
                }.show()
        }

        playerViewModel.reportResult.observe(viewLifecycleOwner) {
            it.doSuccess { data ->
                shortToast(data)
                binding.btnReportError.isSelected = true
            }
            it.doFailure { error -> shortToast(error.errorMsg) }
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