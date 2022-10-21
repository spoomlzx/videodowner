package org.nudt.player.ui.player

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.nudt.player.data.model.Video
import org.nudt.player.databinding.FragmentCommentBinding

private const val ARG_PARAM1 = "param1"

class CommentFragment : Fragment() {
    private var param1: Int = 0

    private val binding by lazy { FragmentCommentBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getInt(ARG_PARAM1)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding.tvContent.text = param1.toString()

        binding.button.setOnClickListener {
            val intent = Intent(context, OnlinePlayerActivity::class.java)
            intent.putExtra("vodId", param1)
            context?.startActivity(intent)
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(vodId: Int) =
            CommentFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, vodId)
                }
            }
    }
}