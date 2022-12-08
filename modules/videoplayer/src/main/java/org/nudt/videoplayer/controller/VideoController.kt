package org.nudt.videoplayer.controller

import android.content.Context
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.viewbinding.ViewBinding
import com.android.iplayer.controller.GestureController
import com.android.iplayer.interfaces.IVideoController
import com.android.iplayer.model.PlayerState
import com.android.iplayer.utils.AnimationUtils
import com.android.iplayer.utils.PlayerUtils
import org.nudt.videoplayer.R
import org.nudt.videoplayer.controls.*
import org.nudt.videoplayer.databinding.PlayerVideoControllerBinding

/**
 * created by hty
 * 2022/6/28
 * Desc:默认的视频交互UI控制器，自定义控制器请继承[GestureController]或BaseController实现自己的视频UI交互控制器
 * 1、此控制器支持手势识别交互，如需自定义控制器请继承[GestureController]或BaseController
 * 2、此控制器只维护屏幕锁功能、点击事件传递
 * 3、如需自定义UI交互组件，请参照[.addControllerWidget]
 */
class VideoController(context: Context?) : GestureController(context) {
    private var mController: View? = null //屏幕锁

    private lateinit var binding: PlayerVideoControllerBinding

    private lateinit var toolBarView: ControlToolBarView
    private lateinit var functionBarView: ControlFunctionBarView

    override fun initBinding(): ViewBinding {
        binding = PlayerVideoControllerBinding.inflate(LayoutInflater.from(context))
        return binding
    }

    override fun initViews() {
        //initWidget()
        super.initViews()
        setDoubleTapTogglePlayEnabled(true) //横屏竖屏状态下都允许双击开始\暂停播放
        initLocker()


    }

    override fun onCreate() {
        super.onCreate()
        initWidget()
        initFunctionBar()
    }

    private fun initWidget() {
        toolBarView = ControlToolBarView(context) //标题栏，返回按钮、视频标题、功能按钮、系统时间、电池电量等组件
        toolBarView.target = IVideoController.TARGET_CONTROL_TOOL
        toolBarView.showBack(false) //是否显示返回按钮,仅限竖屏情况下，横屏模式下强制显示
        val gestureView = ControlGestureView(context) //手势控制屏幕亮度、系统音量、快进、快退UI交互
        val completionView = ControlCompletionView(context) //播放完成、重试
        val statusView = ControlStatusView(context) //移动网络播放提示、播放失败、试看完成
        val loadingView = ControlLoadingView(context) //加载中、开始播放
        val windowView = ControWindowView(context) //悬浮窗窗口播放器的窗口样式
        addControllerWidget(toolBarView, gestureView, completionView, statusView, loadingView, windowView)
    }

    private fun initFunctionBar() {
        functionBarView = ControlFunctionBarView(context) //底部时间、seek、静音、全屏功能栏
        functionBarView.setOnFunctionBarActionListener(object : ControlFunctionBarView.OnFunctionBarActionListener() {
            override fun onSelectSpeed() {
                AnimationUtils.getInstance().startTranslateRightToLocat(binding.llSpeed, animationDuration, null)
                hideWidget(true)
            }

            override fun onSelectVideo() {
                Toast.makeText(context, "选择选集", Toast.LENGTH_SHORT).show()
            }
        })

        addControllerWidget(functionBarView)
    }

    fun setOnToolBarActionListener(onToolBarActionListener: ControlToolBarView.OnToolBarActionListener?) {
        toolBarView.setOnToolBarActionListener(onToolBarActionListener)
    }

    /**
     * 设置屏幕锁功能
     */
    private fun initLocker() {
        //mController = findViewById(R.id.controller_locker)
        binding.controllerLocker.setOnClickListener(OnClickListener {
            stopDelayedRunnable()
            setLocker(!isLocked)
            binding.controllerLockerIc.setImageResource(if (isLocked) R.mipmap.ic_player_locker_true else R.mipmap.ic_player_locker_false)
            Toast.makeText(context, if (isLocked) getString(R.string.player_locker_true) else getString(R.string.player_locker_flase), Toast.LENGTH_SHORT).show()
            if (isLocked) {
                hideWidget(true) //屏幕锁开启时隐藏其它所有控制器
                startDelayedRunnable(MESSAGE_LOCKER_HIDE)
            } else {
                showWidget(true) //屏幕锁关闭时显示其它所有控制器
                startDelayedRunnable(MESSAGE_CONTROL_HIDE)
            }
        })
    }

    public override fun onSingleTap() {
        if (isOrientationPortrait && isListPlayerScene) { //竖屏&&列表模式响应单击事件直接处理为开始\暂停播放事件
            if (null != mVideoPlayerControl) mVideoPlayerControl.togglePlay() //回调给播放器
        } else {
            if (isLocked) {
                //屏幕锁显示、隐藏
                toggleLocker()
            } else {
                //控制器显示、隐藏
                toggleController()
            }
        }
    }

    public override fun onDoubleTap() {
        if (!isLocked) {
            if (null != mVideoPlayerControl) mVideoPlayerControl.togglePlay() //回调给播放器
        }
    }

    override fun onPlayerState(state: PlayerState, message: String) {
        super.onPlayerState(state, message)
        when (state) {
            PlayerState.STATE_RESET, PlayerState.STATE_STOP -> onReset()
            PlayerState.STATE_PREPARE, PlayerState.STATE_BUFFER -> {}
            PlayerState.STATE_START -> {
                startDelayedRunnable(MESSAGE_CONTROL_HIDE)
                if (isOrientationLandscape) { //横屏模式下首次播放显示屏幕锁
                    if (null != mController) mController!!.visibility = VISIBLE
                }
            }
            PlayerState.STATE_PLAY, PlayerState.STATE_ON_PLAY -> startDelayedRunnable(MESSAGE_CONTROL_HIDE)
            PlayerState.STATE_PAUSE, PlayerState.STATE_ON_PAUSE -> stopDelayedRunnable()
            PlayerState.STATE_COMPLETION -> {
                stopDelayedRunnable()
                hideWidget(false)
                hideLockerView()
            }
            PlayerState.STATE_MOBILE -> {}
            PlayerState.STATE_ERROR -> {
                setLocker(false)
                hideLockerView()
            }
            PlayerState.STATE_DESTROY -> onDestroy()
        }
    }

    /**
     * 竖屏状态下,如果用户设置返回按钮可见仅显示返回按钮,切换到横屏模式下播放时初始都不显示
     * @param orientation 更新控制器方向状态 0:竖屏 1:横屏
     */
    override fun onScreenOrientation(orientation: Int) {
        super.onScreenOrientation(orientation)
        if (null != mController) {
            if (isOrientationPortrait) {
                setLocker(false)
                mController!!.visibility = GONE
            } else {
                setLocker(false)
                if (isPlayering) {
                    mController!!.visibility = VISIBLE
                }
            }
        }
    }

    /**
     * 显示\隐藏屏幕锁
     */
    private fun toggleLocker() {
        if (null == mController) return
        stopDelayedRunnable()
        if (mController!!.visibility == VISIBLE) {
            hideLockerView()
        } else {
            AnimationUtils.getInstance().startTranslateRightToLocat(mController, MATION_DRAUTION.toLong(), null)
            startDelayedRunnable(MESSAGE_LOCKER_HIDE)
        }
    }

    /**
     * 显示\隐藏控制器
     */
    private fun toggleController() {
        stopDelayedRunnable()
        if (isControllerShowing) {
            //屏幕锁
            hideLockerView()
            //其它控制器
            hideWidget(true)
        } else {
            //屏幕锁
            if (isOrientationLandscape && null != mController && mController!!.visibility != VISIBLE) {
                AnimationUtils.getInstance().startTranslateRightToLocat(mController, MATION_DRAUTION.toLong(), null)
            }
            showWidget(true)
            startDelayedRunnable()
        }
    }

    /**
     * 结启动延时任务
     */
    override fun startDelayedRunnable() {
        startDelayedRunnable(MESSAGE_CONTROL_HIDE)
    }

    /**
     * 根据消息通道结启动延时任务
     */
    private fun startDelayedRunnable(msg: Int) {
        super.startDelayedRunnable()
        if (null != mExHandel) {
            stopDelayedRunnable()
            val message = mExHandel.obtainMessage()
            message.what = msg
            mExHandel.sendMessageDelayed(message, DELAYED_INVISIBLE.toLong())
        }
    }

    /**
     * 结束延时任务
     */
    override fun stopDelayedRunnable() {
        stopDelayedRunnable(0)
    }

    /**
     * 重新开始延时任务
     */
    override fun reStartDelayedRunnable() {
        super.stopDelayedRunnable()
        stopDelayedRunnable()
        startDelayedRunnable()
    }

    /**
     * 根据消息通道取消延时任务
     * @param msg
     */
    private fun stopDelayedRunnable(msg: Int) {
        if (null != mExHandel) {
            if (0 == msg) {
                mExHandel.removeCallbacksAndMessages(null)
            } else {
                mExHandel.removeMessages(msg)
            }
        }
    }

    /**
     * 使用这个Handel替代getHandel(),避免多播放器同时工作的相互影响
     */
    private val mExHandel: ExHandel? = object : ExHandel(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (MESSAGE_LOCKER_HIDE == msg.what) { //屏幕锁
                hideLockerView()
            } else if (MESSAGE_CONTROL_HIDE == msg.what) { //控制器
                //屏幕锁
                if (isOrientationLandscape) {
                    hideLockerView()
                }
                hideWidget(true)
            }
        }
    }

    /**
     * 隐藏屏幕锁
     */
    private fun hideLockerView() {
        if (null != mController && mController!!.visibility == VISIBLE) {
            AnimationUtils.getInstance().startTranslateLocatToRight(mController, MATION_DRAUTION.toLong()) { mController!!.visibility = GONE }
        }
    }

    /**
     * 设置给用户看的虚拟的视频总时长
     * @param totalDuration 单位：秒
     */
    fun setPreViewTotalDuration(totalDuration: String?) {
        val duration = PlayerUtils.getInstance().parseInt(totalDuration)
        if (duration > 0) preViewTotalDuration = (duration * 1000).toLong()
    }

    /**
     * 是否启用屏幕锁功能(默认开启)，只在横屏状态下可用
     * @param showLocker true:启用 fasle:禁止
     */
    fun showLocker(showLocker: Boolean) {
        binding.controllerRoot.visibility = if (showLocker) VISIBLE else GONE
    }

    /**
     * 重置内部状态
     */
    private fun reset() {
        stopDelayedRunnable()
        mExHandel?.removeCallbacksAndMessages(null)
    }

    override fun onReset() {
        super.onReset()
        reset()
    }

    override fun onDestroy() {
        stopDelayedRunnable()
        super.onDestroy()
        reset()
    }

    companion object {
        private const val MESSAGE_CONTROL_HIDE = 10 //延时隐藏控制器
        private const val MESSAGE_LOCKER_HIDE = 11 //延时隐藏屏幕锁
        private const val DELAYED_INVISIBLE = 5000 //延时隐藏锁时长
        private const val MATION_DRAUTION = 500 //控制器、控制锁等显示\隐藏过渡动画时长(毫秒)
    }
}