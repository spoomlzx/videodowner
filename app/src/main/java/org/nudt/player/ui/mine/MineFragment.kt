package org.nudt.player.ui.mine

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.nudt.player.adapter.MineHistoryAdapter
import org.nudt.player.databinding.FragmentMineBinding
import org.nudt.player.ui.VideoViewModel
import org.nudt.player.ui.download.VideoDownloadListActivity
import org.nudt.player.ui.history.PlayHistoryActivity

class MineFragment : Fragment() {
    private val binding by lazy { FragmentMineBinding.inflate(layoutInflater) }
    private val videoViewModel: VideoViewModel by viewModel()
    private lateinit var historyAdapter: MineHistoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initHistoryView()

        initDownloadView()
    }

    private fun initDownloadView() {
        binding.clDownload.setOnClickListener {
            val intent = Intent(context, VideoDownloadListActivity::class.java)
            context?.startActivity(intent)
        }
    }

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
}