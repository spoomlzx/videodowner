package org.nudt.player.ui.mine

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.nudt.common.SLog
import org.nudt.player.adapter.MineDownloadedAdapter
import org.nudt.player.adapter.MineHistoryAdapter
import org.nudt.player.databinding.FragmentMineBinding
import org.nudt.player.ui.VideoViewModel
import org.nudt.player.ui.download.VideoDownloadListActivity
import org.nudt.player.ui.history.PlayHistoryActivity
import zlc.season.downloadx.DownloadXManager
import zlc.season.downloadx.database.TaskInfo

class MineFragment : Fragment() {
    private val binding by lazy { FragmentMineBinding.inflate(layoutInflater) }
    private val videoViewModel: VideoViewModel by viewModel()
    private lateinit var historyAdapter: MineHistoryAdapter
    private lateinit var downloadedAdapter: MineDownloadedAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initToolbar()

        initHistoryView()

        initDownloadView()
    }

    /**
     * 工具栏
     */
    private fun initToolbar() {
        binding.ivGbook.setOnClickListener {

        }


//        binding.ivConfig.setOnClickListener {
//            val intent = Intent(context, ConfigActivity::class.java)
//            context?.startActivity(intent)
//        }
    }

    /**
     * history 部分
     */
    private fun initHistoryView() {
        binding.clHistory.setOnClickListener {
            val intent = Intent(context, PlayHistoryActivity::class.java)
            context?.startActivity(intent)
        }

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.rvVideoHistory.layoutManager = linearLayoutManager

        context?.let {
            historyAdapter = MineHistoryAdapter(it)
            binding.rvVideoHistory.adapter = historyAdapter
        }

        videoViewModel.historyTop.observe(viewLifecycleOwner) {
            historyAdapter.updateFavoriteList(it)
        }
    }

    /**
     * downloaded video 部分
     */
    private fun initDownloadView() {
        binding.clDownload.setOnClickListener {
            val intent = Intent(context, VideoDownloadListActivity::class.java)
            context?.startActivity(intent)
        }

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.rvVideoDownloaded.layoutManager = linearLayoutManager

        context?.let {
            downloadedAdapter = MineDownloadedAdapter(it)
            binding.rvVideoDownloaded.adapter = downloadedAdapter
        }


    }

    fun fetchDownloadedTaskInfo() {
        DownloadXManager.queryFinishedTaskInfoTopFlow().asLiveData().observe(viewLifecycleOwner) {
            downloadedAdapter.updateTaskInfoList(it)
        }
    }
}