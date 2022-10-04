package org.nudt.player.ui.player

import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import org.nudt.player.utils.SLog
import org.nudt.videoplayer.VideoPlayer

open class BasePlayerActivity : AppCompatActivity() {
    private var isForbidCycle = false //是否开启全屏模式,是否禁止生命周期(悬浮窗必须设置)
    protected lateinit var player: VideoPlayer

    /**
     * 初始化父类的VideoPlayer
     */
    fun initPlayer(player: VideoPlayer) {
        this.player = player
    }

    protected fun forbidCycle() {
        isForbidCycle = true
    }

    /**
     * 由子类调用,告诉父类关心生命周期
     */
    protected fun enableCycle() {
        isForbidCycle = false
    }

    override fun onResume() {
        super.onResume()
        if (!isForbidCycle) player.onResume()
    }

    override fun onPause() {
        super.onPause()
        if (!isForbidCycle) player.onPause()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0) {
            onBackPressed()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * 如果是全屏或者悬浮状态，则不调用super.onBackPressed()
     */
    override fun onBackPressed() {
        SLog.d("onBackPressed-->$isForbidCycle")
        if (!isForbidCycle && !player.isBackPressed) {
            return
        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isForbidCycle) player.onDestroy()
    }


}