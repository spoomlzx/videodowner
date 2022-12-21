package org.nudt.player.ui.home

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.cy.tablayoutniubility.*
import org.nudt.player.R
import org.nudt.player.component.TabMediatorVp2
import org.nudt.player.databinding.FragmentHomeBinding
import org.nudt.player.data.model.VideoType
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

        binding.tvMainAvatar.setOnClickListener {
            val intent = Intent(context, SettingActivity::class.java)
            context?.startActivity(intent)
        }


        return binding.root
    }


    private fun initTabLayout() {
        val viewPager2 = binding.vp2Main
        viewPager2.offscreenPageLimit = 5
        val tabLayoutScroll = binding.tbsMain
        val titles = resources.getStringArray(R.array.sections)
        val fragments = arrayOfNulls<Fragment>(titles.size)

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

        val fragmentPageAdapter: FragPageAdapterVp2<String?> = object : FragPageAdapterVp2<String?>(this) {
            override fun createFragment(bean: String?, position: Int): Fragment {
                if (fragments[position] == null) {
                    when (position) {
                        0 -> fragments[position] = VideoFragment.newInstance(VideoType.MOVIE)
                        1 -> fragments[position] = VideoFragment.newInstance(VideoType.TV)
                        2 -> fragments[position] = VideoFragment.newInstance(VideoType.COMIC)
                        3 -> fragments[position] = VideoFragment.newInstance(VideoType.VARIETY)
                        //4 -> fragments[position] = VideoFragment.newInstance(VideoType.PRIVATE, VideoFragment.HORIZONTAL_PIC)
                        else -> {}
                    }
                }
                return fragments[position]!!
            }

            override fun bindDataToTab(holder: TabViewHolder?, position: Int, bean: String?, isSelected: Boolean) {
                val textView = holder?.getView<TextView>(R.id.tv_tablayout_title)
                textView?.let {
                    if (isSelected) {
                        textView.setTextColor(getColor(context!!, R.color.colorPrimary))
                        textView.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                    } else {
                        textView.setTextColor(getColor(context!!, R.color.text_color))
                        textView.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                    }
                    textView.text = bean
                }

            }

            override fun getTabLayoutID(position: Int, bean: String?): Int {
                return R.layout.tab_item
            }
        }

        val tabAdapter: TabAdapter<String> = TabMediatorVp2<String>(tabLayoutScroll, viewPager2).setAdapter(fragmentPageAdapter)

        val list: ArrayList<String> = ArrayList()
        list.addAll(listOf(*titles))
        fragmentPageAdapter.add<IBaseTabPageAdapter<String?, TabViewHolder>>(list as List<String?>?)
        tabAdapter.add<ITabAdapter<*, *>>(list)

    }

}