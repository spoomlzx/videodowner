package org.nudt.player.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
import com.next.easynavigation.view.EasyNavigationBar.OnTabClickListener
import org.nudt.player.R
import org.nudt.player.databinding.ActivityMainBinding
import org.nudt.player.ui.favorite.FavoriteFragment
import org.nudt.player.ui.home.HomeFragment
import org.nudt.player.ui.mine.MineFragment


class MainActivity : AppCompatActivity() {
    companion object {
        const val CAMERA_REQ_CODE = 111
        const val DEFINED_CODE = 222
        const val BITMAP_CODE = 333
        const val MULTIPROCESSOR_SYN_CODE = 444
        const val MULTIPROCESSOR_ASYN_CODE = 555
        const val GENERATE_CODE = 666
        const val DECODE = 1
        const val GENERATE = 2
        const val REQUEST_CODE_SCAN_ONE = 0X01
        const val REQUEST_CODE_DEFINE = 0X0111
        const val REQUEST_CODE_SCAN_MULTI = 0X011
        const val DECODE_MODE = "decode_mode"
        const val RESULT = "SCAN_RESULT"
    }

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
                        // mineFragment.fetchDownloadedTaskInfo()
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

    /**
     * Call back the permission application result. If the permission application is successful, the barcode scanning view will be displayed.
     * @param requestCode Permission application code.
     * @param permissions Permission array.
     * @param grantResults: Permission application result array.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == GENERATE_CODE) {
//            val intent = Intent(this, GenerateCodeActivity::class.java)
//            this.startActivity(intent)
        }
        if (grantResults.size < 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            return
        }
        //Default View Mode
        if (requestCode == CAMERA_REQ_CODE) {
            ScanUtil.startScan(this, REQUEST_CODE_SCAN_ONE, HmsScanAnalyzerOptions.Creator().create())
        }
    }

    /**
     * Event for receiving the activity result.
     *
     * @param requestCode Request code.
     * @param resultCode Result code.
     * @param data        Result.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK || data == null) {
            return
        }
        //Default View
        if (requestCode == REQUEST_CODE_SCAN_ONE) {
            val obj: HmsScan? = data.getParcelableExtra(ScanUtil.RESULT)
            if (obj != null) {
                Toast.makeText(this, obj.originalValue, Toast.LENGTH_SHORT).show()
            }
        }
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