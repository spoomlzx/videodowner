package org.nudt.player.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.nudt.player.R
import org.nudt.player.adapter.FavoriteAdapter
import org.nudt.player.databinding.FragmentFavoriteBinding
import org.nudt.player.ui.VideoViewModel

class FavoriteFragment : Fragment() {

    private val binding by lazy { FragmentFavoriteBinding.inflate(layoutInflater) }

    private val videoViewModel: VideoViewModel by viewModel()

    private lateinit var adapter: FavoriteAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding.tbCommon.tvTitle.text = getText(R.string.main_nav_favorite)
        initRecyclerView()
        observeData()
        return binding.root
    }

    private fun initRecyclerView() {
        binding.rvVideo.layoutManager = LinearLayoutManager(context)
        context?.let {
            adapter = FavoriteAdapter(it, videoViewModel)
            binding.rvVideo.adapter = adapter
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            observeData()
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            videoViewModel.getFavoriteVideos()?.collectLatest {
                adapter.updateFavoriteList(it)
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }
}