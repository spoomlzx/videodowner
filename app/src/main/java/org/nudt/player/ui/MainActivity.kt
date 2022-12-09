package org.nudt.player.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.next.easynavigation.view.EasyNavigationBar.OnTabClickListener
import org.nudt.common.SLog
import org.nudt.player.R
import org.nudt.player.databinding.ActivityMainBinding
import org.nudt.player.ui.favorite.FavoriteFragment
import org.nudt.player.ui.home.HomeFragment
import org.nudt.player.ui.mine.MineFragment
import zlc.season.downloadx.DownloadXManager


class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val fragments: ArrayList<Fragment> = arrayListOf()
    private val tabText = arrayOf("首页", "收藏", "我的")
    private val normalIcon =
        intArrayOf(R.drawable.ic_vector_tab_bar_home_default, R.drawable.ic_vector_tab_bar_moments_default, R.drawable.ic_vector_tab_bar_mine_default)
    private val selectedIcon =
        intArrayOf(R.drawable.ic_vector_tab_bar_home_selected, R.drawable.ic_vector_tab_bar_moments_selected, R.drawable.ic_vector_tab_bar_mine_selected)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initNavigation()
    }


    /**
     * 初始化底部导航
     */
    private fun initNavigation() {
        fragments.add(HomeFragment())
        fragments.add(FavoriteFragment())
        val mineFragment = MineFragment()
        fragments.add(mineFragment)

        // 设置页面底部导航栏
        binding.enbMain.titleItems(tabText)
            .normalIconItems(normalIcon)
            .selectIconItems(selectedIcon)
            .setOnTabClickListener(object : OnTabClickListener {
                override fun onTabSelectEvent(view: View, position: Int): Boolean {
                    //Tab点击事件  return true 页面不会切换
                    if (position == 2) {
                        mineFragment.fetchDownloadedTaskInfo()
                    }
                    return false
                }

                override fun onTabReSelectEvent(view: View, position: Int): Boolean {
                    //Tab重复点击事件
                    return false
                }
            })
            .normalTextColor(Color.parseColor("#555555"))
            .selectTextColor(Color.parseColor("#ffff6699"))
            .fragmentList(fragments)
            .fragmentManager(supportFragmentManager)
            .navigationHeight(48)
            .lineHeight(1).lineColor(Color.parseColor("#555555"))
            .iconSize(22F)
            .tabTextSize(9)
            .build()
    }

    private var mExitTime: Long = 0

    /**
     * 按两次返回键以后才能退出程序
     */
    override fun onBackPressed() {
        if (System.currentTimeMillis() - mExitTime > 2000) {
            //Snackbar.make(binding.container, "再按一次退出", Snackbar.LENGTH_SHORT).show()
            Toast.makeText(this@MainActivity, "再按一次退出", Toast.LENGTH_SHORT).show()
            mExitTime = System.currentTimeMillis()
        } else {
            // 退出app前暂停所有下载任务
            super.onBackPressed()
        }
    }
}