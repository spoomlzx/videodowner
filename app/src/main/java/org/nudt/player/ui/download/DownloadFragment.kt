package org.nudt.player.ui.download

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.jeffmony.downloader.VideoDownloadManager
import com.jeffmony.downloader.listener.IDownloadInfosCallback
import com.jeffmony.downloader.model.VideoTaskItem
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.coroutines.launch
import org.nudt.player.R
import org.nudt.player.adapter.DownloadedAdapter
import org.nudt.player.databinding.FragmentDownloadBinding
import org.nudt.player.utils.SLog

class DownloadFragment : Fragment() {
    private val binding by lazy { FragmentDownloadBinding.inflate(layoutInflater) }
    private lateinit var adapter: DownloadedAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding.tbCommon.tvTitle.text = getString(R.string.main_menu_offline_download)
        SLog.d("created download fragment")

        requestPermission()
        initRecyclerView()

        // 异步获取所有下载任务
        VideoDownloadManager.getInstance().fetchDownloadItems(mDownloadInfosCallback);

        // 点击进入下载列表
        binding.llDownloading.setOnClickListener {
            val intent = Intent(context, DownloadingActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    /**
     * 显示已缓存视频列表
     */
    private fun initRecyclerView() {
        context?.let {
            binding.rvVideoDownloaded.layoutManager = LinearLayoutManager(context)
            adapter = DownloadedAdapter(it)
            binding.rvVideoDownloaded.adapter = adapter
        }
    }


    private val mDownloadInfosCallback = IDownloadInfosCallback { items: List<VideoTaskItem> ->
        val downloadedTaskItems = mutableListOf<VideoTaskItem>()
        val downloadingTaskItems = mutableListOf<VideoTaskItem>()

        for (item in items) {
            if (item.isSuccessState) {
                downloadedTaskItems.add(item)
            } else {
                downloadingTaskItems.add(item)
            }
        }
        SLog.d("get download tasks success, ready to update view")
        updateView(downloadedTaskItems, downloadingTaskItems)
    }

    /**
     * 根据返回的task list 更新视图
     */
    private fun updateView(downloadedTaskItems: MutableList<VideoTaskItem>, downloadingTaskItems: MutableList<VideoTaskItem>) {
        lifecycleScope.launch {
            if (downloadedTaskItems.size > 0) {
                binding.llDownloaded.visibility = View.VISIBLE
                adapter.setData(downloadedTaskItems)
            } else {
                binding.llDownloaded.visibility = View.GONE
            }

            // 如果有正在下载任务，则显示
            if (downloadingTaskItems.size > 0) {
                binding.llDownloading.visibility = View.VISIBLE
                binding.tvDownloadingVideoNum.text = "共${downloadingTaskItems.size}个内容"
                val firstTaskItem = downloadingTaskItems.first()
                context?.let {
                    val pic = if (firstTaskItem.coverPath != null) firstTaskItem.coverPath else firstTaskItem.coverUrl
                    Glide.with(it).load(pic).placeholder(R.drawable.default_image).into(binding.ivDownloadingVideoPic)
                    binding.tvDownloadingTitle.text = firstTaskItem.title
                }
            } else {
                // 没有正在下载任务，隐藏相关元素
                binding.llDownloading.visibility = View.GONE
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        VideoDownloadManager.getInstance().removeDownloadInfosCallback(mDownloadInfosCallback)
    }

    /**
     * 请求磁盘读写权限
     */
    private fun requestPermission() {
        val rxPermissions: RxPermissions = RxPermissions(this@DownloadFragment);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
            .subscribe { granted: Boolean ->
                if (granted) { // Always true pre-M
                    SLog.d("request permission success")
                } else {
                    SLog.e("request permission error")
                }
            }
    }
}