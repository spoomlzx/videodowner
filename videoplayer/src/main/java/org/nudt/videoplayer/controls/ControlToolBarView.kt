package org.nudt.videoplayer.controls

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.android.iplayer.media.IMediaPlayer
import com.android.iplayer.model.PlayerState
import com.android.iplayer.utils.AnimationUtils
import com.android.iplayer.utils.PlayerUtils
import org.nudt.videoplayer.BaseControlWidget
import org.nudt.videoplayer.databinding.PlayerControlToolarBinding
import org.nudt.videoplayer.view.BatteryView

/**
 * created by hty
 * 2022/8/22
 * Desc:UI控制器-标题栏，这个标题栏维护有返回按钮、电池电量(横屏)、其它功能菜单
 * 1、单击播放器空白位置时控制器需要处理显示\隐藏逻辑的需要复写[.showControl]和[.hideControl]方法
 */
class ControlToolBarView(context: Context?) : BaseControlWidget(context), View.OnClickListener {
    private var mController: View? = null//控制器

    //记录用户的设置，是否显示返回按钮\投屏按钮\悬浮窗按钮\菜单按钮
    private var showBack = false
    private var showTv = false
    private var showWindow = false
    private var showMenu = false

    private lateinit var binding: PlayerControlToolarBinding

    override fun initBinding() {
        binding = PlayerControlToolarBinding.inflate(LayoutInflater.from(context), this, true)
    }

    override fun initViews() {
        hide()
        binding.controllerTitleBack.setOnClickListener(this)
        binding.controllerTitleTv.setOnClickListener(this)
        binding.controllerTitleWindow.setOnClickListener(this)
        binding.controllerTitleMenu.setOnClickListener(this)
        mController = binding.controllerTitleBar
    }

    override fun onClick(view: View) {
        when (view) {
            binding.controllerTitleBack -> {
                if (null != mControlWrapper) {
                    if (mControlWrapper.isOrientationPortrait) {
                        if (null != mOnToolBarActionListener) mOnToolBarActionListener!!.onBack()
                    } else {
                        mControlWrapper.quitFullScreen()
                    }
                }
            }
            binding.controllerTitleTv -> {
                reStartDelayedRunnable()
                if (null != mOnToolBarActionListener) mOnToolBarActionListener!!.onTv()
            }
            binding.controllerTitleWindow -> {
                reStartDelayedRunnable()
                if (null != mOnToolBarActionListener) mOnToolBarActionListener!!.onWindow()
            }
            binding.controllerTitleMenu -> {
                reStartDelayedRunnable()
                if (null != mOnToolBarActionListener) mOnToolBarActionListener!!.onMenu()
            }
        }
    }

    /**
     * @param isAnimation 控制器显示,是否开启动画
     */
    override fun showControl(isAnimation: Boolean) {
        if (mController != null && mController!!.visibility != VISIBLE) {
            if (isAnimation) {
                AnimationUtils.getInstance().startTranslateTopToLocat(mController, animationDuration, null)
            } else {
                mController!!.visibility = VISIBLE
            }
        }
    }

    /**
     * @param isAnimation 控制器隐藏,是否开启动画
     */
    override fun hideControl(isAnimation: Boolean) {
        if (null != mController) {
            if (mController!!.visibility != GONE) {
                if (isAnimation) {
                    AnimationUtils.getInstance().startTranslateLocatToTop(mController, animationDuration) { mController!!.visibility = GONE }
                } else {
                    mController!!.visibility = GONE
                }
            }
        }
    }

    override fun onPlayerState(state: PlayerState, message: String) {
        when (state) {
            PlayerState.STATE_RESET, PlayerState.STATE_STOP -> onReset()
            PlayerState.STATE_START -> {
                //渲染第一帧时，竖屏和横屏都显示
                if (isNoimalScene) {
                    show()
                }
                showControl(true)
            }
            PlayerState.STATE_PREPARE -> hide()
            else -> {}
        }
    }

    override fun onOrientation(direction: Int) {
        if (null == mController) return
        if (IMediaPlayer.ORIENTATION_LANDSCAPE == direction) {
            show()
            binding.controllerTitle.visibility = VISIBLE //视频标题
            binding.controllerTitleBack.visibility = VISIBLE //返回按钮

            binding.controllerTitleWindow.visibility = GONE

            //添加系统时间\电池电量组件
            val controllerBattery = binding.controllerBattery
            controllerBattery.visibility = VISIBLE
            controllerBattery.addView(BatteryView(context))
            //竖屏下处理标题栏和控制栏的左右两侧缩放
            val margin = PlayerUtils.getInstance().dpToPxInt(22f)
            mController!!.setPadding(margin, 0, margin, 0)
            if (isPlaying) reStartDelayedRunnable()
        } else {
            binding.controllerTitle.visibility = GONE //视频标题
            binding.controllerTitleBack.visibility = if (showBack) VISIBLE else GONE //返回按钮
            binding.controllerTitleWindow.visibility = VISIBLE
            //移除系统时间\电池电量组件
            val controllerBattery = binding.controllerBattery
            controllerBattery.removeAllViews()
            controllerBattery.visibility = GONE
            mController!!.setPadding(0, 0, 0, 0)
            if (isNoimalScene) {
                show()
            } else {
                //非常规情况下不处理
                hide()
            }
        }
    }

    override fun onPlayerScene(playerScene: Int) {
        //当播放器和控制器在专场播放、场景发生变化时，仅当在常规模式下并且正在播放才显示控制器
        if (isNoimalScene) {
            show()
            if (isPlaying) {
                showControl(false)
                reStartDelayedRunnable()
            }
        } else {
            hide()
        }
    }

    override fun setTitle(title: String) {
        binding.controllerTitle.text = PlayerUtils.getInstance().formatHtml(title)
    }

    /**
     * 是否显示返回按钮，仅限竖屏情况下，横屏模式下强制显示
     *
     * @param showBack 返回按钮是否显示
     */
    fun showBack(showBack: Boolean) {
        this.showBack = showBack
        binding.controllerTitleBack.visibility = if (isOrientationLandscape) VISIBLE else if (showBack) VISIBLE else GONE //返回按钮
    }

    /**
     * 是否显示投屏\悬浮窗\功能等按钮，仅限竖屏情况下，横屏模式下强制不显示
     *
     * @param showTv     投屏按钮是否显示
     * @param showWindow 悬浮窗按钮是否显示
     * @param showMenu   菜单按钮是否显示
     */
    fun showMenus(showTv: Boolean, showWindow: Boolean, showMenu: Boolean) {
        this.showTv = showTv
        this.showWindow = showWindow
        this.showMenu = showMenu
        binding.controllerTitleTv.visibility = if (isOrientationLandscape) GONE else if (showTv) VISIBLE else GONE //投屏
        binding.controllerTitleWindow.visibility = if (isOrientationLandscape) GONE else if (showWindow) VISIBLE else GONE //悬浮窗
        binding.controllerTitleMenu.visibility = if (isOrientationLandscape) VISIBLE else if (showMenu) VISIBLE else GONE //菜单按钮
    }

    abstract class OnToolBarActionListener {
        open fun onBack() {}
        open fun onTv() {}
        open fun onWindow() {}
        open fun onMenu() {}
    }

    private var mOnToolBarActionListener: OnToolBarActionListener? = null
    fun setOnToolBarActionListener(onToolBarActionListener: OnToolBarActionListener?) {
        mOnToolBarActionListener = onToolBarActionListener
    }

    override fun onReset() {
        super.onReset()
        hideControl(false)
    }

}