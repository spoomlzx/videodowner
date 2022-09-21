package org.nudt.player.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.jeffmony.downloader.VideoDownloadManager
import com.next.easynavigation.view.EasyNavigationBar.OnTabClickListener
import org.nudt.player.R
import org.nudt.player.databinding.ActivityMainBinding
import org.nudt.player.ui.download.DownloadFragment
import org.nudt.player.ui.favorite.FavoriteFragment
import org.nudt.player.ui.history.HistoryFragment
import org.nudt.player.ui.home.HomeFragment


class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val fragments: ArrayList<Fragment> = arrayListOf()
    private val tabText = arrayOf("首页", "收藏", "下载", "拉取")
    private val normalIcon =
        intArrayOf(R.drawable.vector_home_n, R.drawable.vector_favorite_n, R.drawable.vector_download_n, R.drawable.vector_history_n)
    private val selectedIcon =
        intArrayOf(R.drawable.vector_home, R.drawable.vector_favorite, R.drawable.vector_download, R.drawable.vector_history)


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
        fragments.add(DownloadFragment())
        fragments.add(HistoryFragment())

        // 设置页面底部导航栏
        binding.enbMain.titleItems(tabText)
            .normalIconItems(normalIcon)
            .selectIconItems(selectedIcon)
            .setOnTabClickListener(object : OnTabClickListener {
                override fun onTabSelectEvent(view: View, position: Int): Boolean {
                    //Tab点击事件  return true 页面不会切换
                    if (position == 2) {
                        VideoDownloadManager.getInstance().fetchDownloadItems()
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
            VideoDownloadManager.getInstance().pauseAllDownloadTasks()
            super.onBackPressed()
        }
    }
}