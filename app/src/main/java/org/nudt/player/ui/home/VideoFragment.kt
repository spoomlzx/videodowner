package org.nudt.player.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.nudt.common.SLog
import org.nudt.player.adapter.VideoPagingAdapter
import org.nudt.player.databinding.FragmentVideoBinding
import org.nudt.player.ui.VideoViewModel

private const val TYPE_PARAM = "type_id"

class VideoFragment : Fragment() {
    private var type: Int = 1

    private val binding by lazy { FragmentVideoBinding.inflate(layoutInflater) }

    private val videoViewModel: VideoViewModel by viewModel()

    private lateinit var adapter: VideoPagingAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        context?.let {
            adapter = VideoPagingAdapter(it, videoViewModel)
            binding.rvVideo.adapter = adapter
            binding.rvVideo.layoutManager = GridLayoutManager(it, 3)
            binding.swipeRefreshLayout.setOnRefreshListener {
                adapter.refresh()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        type = requireArguments().getInt(TYPE_PARAM)
        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collect { loadStates ->
                //根据刷新状态来通知swipeRefreshLayout是否刷新完毕
                binding.swipeRefreshLayout.isRefreshing = loadStates.mediator?.refresh is LoadState.Loading
            }
        }

        lifecycleScope.launchWhenCreated {
            // 更新页面数据
            videoViewModel.bindHomePage(type).collectLatest {
                adapter.submitData(it)
            }
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param type Parameter 1.
         * @return A new instance of fragment VideoFragment.
         */
        @JvmStatic
        fun newInstance(type: Int) = VideoFragment().apply {
            arguments = Bundle().apply {
                putInt(TYPE_PARAM, type)
            }
        }
    }
}