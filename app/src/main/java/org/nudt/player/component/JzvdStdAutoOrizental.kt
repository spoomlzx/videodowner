package org.nudt.player.component

import android.content.Context
import android.content.pm.ActivityInfo
import android.util.AttributeSet
import cn.jzvd.JzvdStd

/**
 * 根据视频长宽，最大化时自适应横屏竖屏
 */
class JzvdStdAutoOrizental : JzvdStd {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    override fun onVideoSizeChanged(width: Int, height: Int) {
        super.onVideoSizeChanged(width, height)
        if (width > 0 && height > 0) {
            if (height > width) {
                FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } else {
                FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
        }
    }

    override fun reset() {
        super.reset()
        FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}