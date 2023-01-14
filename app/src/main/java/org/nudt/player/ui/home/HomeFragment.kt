package org.nudt.player.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import org.nudt.player.R
import org.nudt.player.data.model.VideoType
import org.nudt.player.databinding.FragmentHomeBinding
import org.nudt.player.ui.search.SearchActivity
import org.nudt.player.ui.setting.SettingActivity
import java.lang.reflect.Field

class HomeFragment : Fragment() {

    private val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        initTabLayout()

        binding.tvMainSearch.setOnClickListener {
            val intent = Intent(context, SearchActivity::class.java)
            context?.startActivity(intent)
        }

        return binding.root
    }


    private fun initTabLayout() {
        val viewPager2 = binding.vp2Main
        viewPager2.offscreenPageLimit = 5
        val titles = resources.getStringArray(R.array.sections)

        viewPager2.adapter = object : FragmentStateAdapter(this@HomeFragment) {
            override fun getItemCount(): Int {
                return 4
            }

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> VideoFragment.newInstance(VideoType.MOVIE)
                    1 -> VideoFragment.newInstance(VideoType.TV)
                    2 -> VideoFragment.newInstance(VideoType.COMIC)
                    3 -> VideoFragment.newInstance(VideoType.VARIETY)
                    else -> VideoFragment.newInstance(VideoType.VARIETY)
                }
            }
        }

        TabLayoutMediator(binding.tabs, viewPager2, true, true) { tab, position ->
            tab.text = titles[position]
        }.attach()

        // 这段代码可以降低viewPager2的左右滑动灵敏度
        try {
            val recyclerViewField: Field = ViewPager2::class.java.getDeclaredField("mRecyclerView")
            recyclerViewField.isAccessible = true
            val recyclerView = recyclerViewField.get(viewPager2) as RecyclerView
            val touchSlopField: Field = RecyclerView::class.java.getDeclaredField("mTouchSlop")
            touchSlopField.isAccessible = true
            val touchSlop = touchSlopField.get(recyclerView) as Int
            touchSlopField.set(recyclerView, touchSlop * 4)
        } catch (ignore: java.lang.Exception) {
        }
    }

}