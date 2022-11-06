package org.nudt.player.ui.mine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import org.nudt.player.R
import org.nudt.player.databinding.FragmentHistoryBinding
import org.nudt.common.SLog
import org.nudt.player.databinding.FragmentMineBinding

class MineFragment : Fragment() {
    private val binding by lazy { FragmentMineBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.rvVideoHistory.layoutManager = linearLayoutManager


    }
}