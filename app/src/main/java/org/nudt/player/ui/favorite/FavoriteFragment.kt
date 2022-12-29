package org.nudt.player.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
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
        binding.tbCommon.tvTitle.text = getText(R.string.favorite)
        binding.tbCommon.ivBack.visibility = View.GONE
        initRecyclerView()
        return binding.root
    }

    private fun initRecyclerView() {
        binding.rvVideo.layoutManager = LinearLayoutManager(context)
        context?.let {
            adapter = FavoriteAdapter(it, videoViewModel)
            binding.rvVideo.adapter = adapter
        }

        videoViewModel.favorites.observe(viewLifecycleOwner) {
            adapter.updateFavoriteList(it)
        }
    }
}