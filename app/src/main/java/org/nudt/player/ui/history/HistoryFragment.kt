package org.nudt.player.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.nudt.player.R
import org.nudt.player.databinding.FragmentHistoryBinding
import org.nudt.common.SLog

class HistoryFragment : Fragment() {
    private val binding by lazy { FragmentHistoryBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding.tbCommon.tvTitle.text = getString(R.string.main_menu_history)
        SLog.d("created history fragment")
        return binding.root
    }
}