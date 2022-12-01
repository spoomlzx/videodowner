package org.nudt.player.ui.history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.nudt.player.adapter.HistoryAdapter
import org.nudt.player.databinding.ActivityPlayHistoryBinding
import org.nudt.player.ui.VideoViewModel

class PlayHistoryActivity : AppCompatActivity() {
    private val binding by lazy { ActivityPlayHistoryBinding.inflate(layoutInflater) }
    private lateinit var historyAdapter: HistoryAdapter

    private val videoViewModel: VideoViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.tbCommon.tvTitle.text = "历史记录"
        // 左上角返回按钮
        binding.tbCommon.ivBack.setOnClickListener { finish() }

        initHistoryView()
    }

    private fun initHistoryView() {
        val linearLayoutManager = LinearLayoutManager(this@PlayHistoryActivity)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvVideoHistory.layoutManager = linearLayoutManager

        historyAdapter = HistoryAdapter(this@PlayHistoryActivity, videoViewModel)
        binding.rvVideoHistory.adapter = historyAdapter

        videoViewModel.history.observe(this) {
            historyAdapter.updateFavoriteList(it)
        }
    }
}