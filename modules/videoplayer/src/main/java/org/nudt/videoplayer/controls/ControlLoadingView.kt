package org.nudt.videoplayer.controls

import android.content.Context
import android.view.View
import android.view.View.OnClickListener
import android.widget.ProgressBar
import com.android.iplayer.base.BaseControlWidget
import com.android.iplayer.model.PlayerState
import org.nudt.videoplayer.R

/**
 * created by hty
 * 2022/8/22
 * Desc:UI控制器-加载、暂停、初始状态
 * 1、为配合窗口样式，当播放器处于窗口样式时此空间不可见
 */
class ControlLoadingView(context: Context?) : BaseControlWidget(context) {
    private var mLoadingView //加载中
            : ProgressBar? = null
    private var mControllerPlay //播放按钮
            : View? = null

    override fun getLayoutId(): Int {
        return R.layout.player_control_loading
    }

    override fun initViews() {
        mLoadingView = findViewById(R.id.controller_loading)
        mControllerPlay = findViewById(R.id.controller_play)
        mControllerPlay?.setOnClickListener(OnClickListener { togglePlay() })
    }

    override fun onPlayerState(state: PlayerState, message: String) {
        when (state) {
            PlayerState.STATE_RESET, PlayerState.STATE_STOP, PlayerState.STATE_PAUSE, PlayerState.STATE_ON_PAUSE -> changedUi(GONE, VISIBLE)
            PlayerState.STATE_PREPARE, PlayerState.STATE_BUFFER -> changedUi(VISIBLE, GONE)
            else -> changedUi(GONE, GONE)
        }
    }

    override fun onOrientation(direction: Int) {}
    override fun onPlayerScene(playerScene: Int) {
        if (isWindowScene(playerScene)) {
            hide()
        } else {
            show()
        }
    }

    /**
     * 改变UI状态
     * @param loading 加载中
     * @param playIcon 播放按钮
     */
    private fun changedUi(loading: Int, playIcon: Int) {
        if (null != mLoadingView) mLoadingView!!.visibility = loading
        if (null != mControllerPlay) mControllerPlay!!.visibility = playIcon
    }
}