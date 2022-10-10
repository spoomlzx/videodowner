package org.nudt.player.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.nudt.player.R
import org.nudt.player.data.model.PlayUrl
import org.nudt.player.databinding.FragmentCommentBinding
import org.nudt.player.ui.VideoViewModel
import org.nudt.player.utils.SLog

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CommentFragment(val viewModel: VideoViewModel) : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private val binding by lazy { FragmentCommentBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding.tvContent.text = param1
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(viewModel: VideoViewModel, id: String?, param2: String?) =
            CommentFragment(viewModel).apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, id)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}