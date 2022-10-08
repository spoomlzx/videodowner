package org.nudt.player.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import org.nudt.player.R
import org.nudt.player.databinding.FragmentCommentBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CommentFragment : Fragment() {
    private var param1: Int? = null
    private var param2: String? = null

    private val binding by lazy { FragmentCommentBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getInt(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        parentFragmentManager.commit {
            setReorderingAllowed(true)
            add<VideoDescriptionFragment>(R.id.fragment_container_view)
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(id: Int, param2: String?) =
            CommentFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, id)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}