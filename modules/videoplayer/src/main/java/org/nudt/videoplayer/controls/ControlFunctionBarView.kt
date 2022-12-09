package org.nudt.videoplayer.controls

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import com.android.iplayer.controller.ControlWrapper
import com.android.iplayer.media.IMediaPlayer
import com.android.iplayer.model.PlayerState
import com.android.iplayer.utils.AnimationUtils
import com.android.iplayer.utils.PlayerUtils
import org.nudt.videoplayer.R
import org.nudt.videoplayer.databinding.PlayerControlFunctionbarBinding

/**
 * created by hty
 * 2022/8/22
 * Desc:UI控制器-底部功能交互控制
 * 1、自定义seekbar相关的控制器需要实现[.isSeekBarShowing]方法，返回显示状态给Controller判断控制器是否正在显示中
 * 2、当单击BaseController空白区域时控制器需要处理显示\隐藏逻辑的情况下需要复写[.showControl]和[.hideControl]方法
 * 3、这个seekBar进度条组件还维护了底部的ProgressBar，SDK默认的UI交互是：当播放器处于列表模式时不显示，其它情况都显示
 */
class ControlFunctionBarView(context: Context?) : BaseControlWidget(context), View.OnClickListener {
    private lateinit var mController: View //控制器
    private lateinit var mSeekBar: SeekBar //seek调节控制器
    private lateinit var mProgressBar: ProgressBar //底部副进度条
    private lateinit var mCurrentDuration: TextView
    private lateinit var mTotalDuration: TextView //当前播放位置时间\总时间
    private lateinit var mPlayIcon: ImageView //左下角的迷你播放状态按钮
    private lateinit var mPlayIconLand: ImageView //横屏时左下角的迷你播放状态按钮

    //用户手指是否持续拖动中
    private var isTouchSeekBar = false

    private lateinit var binding: PlayerControlFunctionbarBinding


    override fun initBinding() {
        binding = PlayerControlFunctionbarBinding.inflate(LayoutInflater.from(context), this, true)
    }

    override fun initViews() {
        hide()
        mPlayIcon = binding.controllerStart
        mPlayIconLand = binding.controllerStartLand
        mSeekBar = binding.controllerSeekBar
        mController = binding.llController
        mCurrentDuration = binding.controllerCurrentDuration
        mTotalDuration = binding.controllerTotalDuration
        mProgressBar = binding.controllerBottomProgress
        mPlayIcon.setOnClickListener(this)
        mPlayIconLand.setOnClickListener(this)
        binding.controllerBtnFullscreen.setOnClickListener(this)
        binding.controllerCurrentSpeed.setOnClickListener(this)
        binding.controllerTotalList.setOnClickListener(this)
        //seekBar监听
        mSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            /**
             * 用户持续拖动进度条,视频总长为虚拟时长时，用户不得滑动阈值超过限制
             * @param seekBar
             * @param progress
             * @param fromUser
             */
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                //                ILogger.d(TAG,"onProgressChanged-->progress:"+progress+",fromUser:"+fromUser+getOrientationStr());
                //视频虚拟总长度
                mCurrentDuration.text = PlayerUtils.getInstance().stringForAudioTime(progress.toLong())
                mProgressBar.progress = progress
            }

            /**
             * 获得焦点-按住了
             * @param seekBar
             */
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                isTouchSeekBar = true
                mControlWrapper.stopDelayedRunnable() //取消定时隐藏任务
            }

            /**
             * 失去焦点-松手了
             * @param seekBar
             */
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                isTouchSeekBar = false
                mControlWrapper.startDelayedRunnable() //开启定时隐藏任务
                //当controller_deblocking设置了点击时间，试看结束的拦截都无效
                //                ILogger.d(TAG,"onStopTrackingTouch-->,isCompletion:"+isCompletion+",preViewTotalTime:"+mPreViewTotalTime);
                if (null != mControlWrapper) {
                    if (mControlWrapper.isCompletion && mControlWrapper.preViewTotalDuration > 0) { //拦截是看结束,让用户解锁
                        if (null != mControlWrapper) mControlWrapper.onCompletion()
                        return
                    }
                    val seekBarProgress = seekBar.progress
                    //                    ILogger.d(TAG,"onStopTrackingTouch-->seekBarProgress:"+seekBarProgress+",ViewTotalTime:"+ mPreViewTotalTime +",duration:"+ mVideoPlayerControl.getDurtion()+getOrientationStr());
                    if (mControlWrapper.preViewTotalDuration > 0) { //跳转至某处,如果滑动的时长超过真实的试看时长,则直接播放完成需要解锁
                        val durtion = mControlWrapper.duration
                        if (0 == seekBarProgress) { //重新从头开始播放
                            //改变UI为缓冲状态
                            mControlWrapper.onPlayerState(PlayerState.STATE_BUFFER, "seek")
                            mControlWrapper.seekTo(0)
                        } else {
                            if (seekBarProgress >= durtion) { //试看片段,需要解锁
                                mControlWrapper.onCompletion()
                            } else {
                                //改变UI为缓冲状态
                                mControlWrapper.onPlayerState(PlayerState.STATE_BUFFER, "seek")
                                mControlWrapper.seekTo(seekBarProgress.toLong()) //试看片段内,允许跳转
                            }
                        }
                    } else {
                        //改变UI为缓冲状态
                        mControlWrapper.onPlayerState(PlayerState.STATE_BUFFER, "seek")
                        mControlWrapper.seekTo(seekBarProgress.toLong()) //真实时长,允许跳转
                    }
                }
            }
        })
    }

    abstract class OnFunctionBarActionListener {
        open fun onClickSpeed() {}
        open fun onClickVideo() {}
    }

    private var mOnFunctionBarActionListener: OnFunctionBarActionListener? = null
    fun setOnFunctionBarActionListener(onFunctionBarActionListener: OnFunctionBarActionListener?) {
        mOnFunctionBarActionListener = onFunctionBarActionListener
    }




    override fun attachControlWrapper(controlWrapper: ControlWrapper) {
        super.attachControlWrapper(controlWrapper)
        mTotalDuration.text = PlayerUtils.getInstance().stringForAudioTime(mControlWrapper.preViewTotalDuration)
    }

    override fun onClick(view: View) {
        when (view) {
            binding.controllerStart, binding.controllerStartLand -> {
                reStartDelayedRunnable()
                togglePlay()
            }
            binding.controllerBtnFullscreen -> {
                toggleFullScreen()
                reStartDelayedRunnable()
            }
            binding.controllerCurrentSpeed -> {
                if (null != mOnFunctionBarActionListener) mOnFunctionBarActionListener!!.onClickSpeed()
            }
            binding.controllerTotalList -> {
                if (null != mOnFunctionBarActionListener) mOnFunctionBarActionListener!!.onClickVideo()
            }
        }
    }

    override fun isSeekBarShowing(): Boolean {
        return mController.visibility == VISIBLE
    }

    /**
     * @param isAnimation 控制器显示,是否开启动画
     */
    override fun showControl(isAnimation: Boolean) {
        if (mController.visibility != VISIBLE) {
            mProgressBar.visibility = GONE
            if (isAnimation) {
                AnimationUtils.getInstance().startTranslateBottomToLocat(mController, animationDuration, null)
            } else {
                mController.visibility = VISIBLE
            }
        }
    }

    /**
     * @param isAnimation 控制器隐藏,是否开启动画
     */
    override fun hideControl(isAnimation: Boolean) {
        if (mController.visibility != GONE) {
            if (isAnimation) {
                AnimationUtils.getInstance().startTranslateLocatToBottom(mController, animationDuration) {
                    mController.visibility = GONE
                    AnimationUtils.getInstance().startAlphaAnimatioFrom(mProgressBar, animationDuration, false, null)
                    //                            if(null!=mProgressBar) mProgressBar.setVisibility(View.VISIBLE);
                }
            } else {
                mController.visibility = GONE
                mProgressBar.visibility = VISIBLE
            }
        }
    }

    override fun onPlayerState(state: PlayerState, message: String) {
        when (state) {
            PlayerState.STATE_RESET, PlayerState.STATE_STOP -> onReset()
            PlayerState.STATE_PREPARE -> {
                mPlayIcon.setImageResource(R.mipmap.ic_player_play)
                mPlayIconLand.setImageResource(R.mipmap.ic_player_play)
                hide()
            }
            PlayerState.STATE_BUFFER, PlayerState.STATE_PAUSE, PlayerState.STATE_ON_PAUSE -> {
                mPlayIcon.setImageResource(R.mipmap.ic_player_play)
                mPlayIconLand.setImageResource(R.mipmap.ic_player_play)
            }
            PlayerState.STATE_START -> {
                //渲染第一帧时，竖屏和横屏都显示
                if (isNoimalScene) {
                    show()
                }
                mPlayIcon.setImageResource(R.mipmap.ic_player_pause)
                mPlayIconLand.setImageResource(R.mipmap.ic_player_pause)
                showControl(true)
            }
            PlayerState.STATE_PLAY, PlayerState.STATE_ON_PLAY -> {
                mPlayIcon.setImageResource(R.mipmap.ic_player_pause)
                mPlayIconLand.setImageResource(R.mipmap.ic_player_pause)
            }
            PlayerState.STATE_COMPLETION -> {
                mPlayIcon.setImageResource(R.mipmap.ic_player_play)
                mPlayIconLand.setImageResource(R.mipmap.ic_player_play)
                resetProgressBar()
            }
            PlayerState.STATE_MOBILE -> {}
            PlayerState.STATE_ERROR -> {
                mPlayIcon.setImageResource(R.mipmap.ic_player_play)
                mPlayIconLand.setImageResource(R.mipmap.ic_player_play)
                onReset()
            }
            PlayerState.STATE_DESTROY -> onDestroy()
        }
    }

    override fun onOrientation(direction: Int) {
        if (IMediaPlayer.ORIENTATION_LANDSCAPE == direction) {
            val margin22 = PlayerUtils.getInstance().dpToPxInt(22f)
            //横屏下处理标题栏和控制栏的左右两侧缩放
            mController.setPadding(margin22, 0, margin22, 0)

            binding.llLand.visibility = VISIBLE
            binding.controllerStart.visibility = GONE
            // 横屏时不显示最大化按钮
            binding.controllerBtnFullscreen.visibility = GONE

            show()
            if (isPlaying) reStartDelayedRunnable()
        } else {
            val margin5 = PlayerUtils.getInstance().dpToPxInt(5f)
            mController.setPadding(margin5, 0, margin5, 0)

            binding.llLand.visibility = GONE
            if (isNoimalScene) {
                binding.controllerStart.visibility = VISIBLE
                binding.controllerBtnFullscreen.visibility = VISIBLE
                show()
            } else {
                //非常规场景不处理
                hide()
            }
        }
    }

    override fun onPlayerScene(playerScene: Int) {
        binding.controllerProgress.visibility = if (isListPlayerScene) GONE else VISIBLE
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

    override fun onProgress(currentDurtion: Long, totalDurtion: Long) {
        if (null != mControlWrapper) {
            if (mProgressBar.max == 0) {
                mProgressBar.max = (if (mControlWrapper.preViewTotalDuration > 0) mControlWrapper.preViewTotalDuration else totalDurtion).toInt()
            }
            if (mSeekBar.max <= 0) { //总进度总时长只更新一次,如果是虚拟的总时长,则在setViewTotalDuration中更新总时长
                mSeekBar.max = (if (mControlWrapper.preViewTotalDuration > 0) mControlWrapper.preViewTotalDuration else totalDurtion).toInt()
                mTotalDuration.text =
                    PlayerUtils.getInstance().stringForAudioTime(if (mControlWrapper.preViewTotalDuration > 0) mControlWrapper.preViewTotalDuration else totalDurtion)
            }
            if (!isTouchSeekBar) mSeekBar.progress = currentDurtion.toInt()
        }
    }

    override fun onBuffer(bufferPercent: Int) {
        if (null != mControlWrapper) {
            val percent = PlayerUtils.getInstance().formatBufferPercent(bufferPercent, mControlWrapper.duration)
            if (mSeekBar.secondaryProgress != percent) {
                mSeekBar.secondaryProgress = percent
            }
            if (mProgressBar.secondaryProgress != percent) {
                mProgressBar.secondaryProgress = percent
            }
        }
    }

    /**
     * 是否启用全屏按钮播放功能
     * @param enable true:启用 false:禁止 默认是开启的
     */
    fun enableFullScreen(enable: Boolean) {
        binding.controllerBtnFullscreen.visibility = if (enable) VISIBLE else GONE
    }

    private fun resetProgressBar() {
        mProgressBar.progress = 0
        mProgressBar.secondaryProgress = 0
        mProgressBar.max = 0
    }

    override fun onReset() {
        mSeekBar.progress = 0
        mSeekBar.secondaryProgress = 0
        mSeekBar.max = 0
        resetProgressBar()
        hideControl(false)
        mTotalDuration.text = PlayerUtils.getInstance().stringForAudioTime(0)
        mCurrentDuration.text = PlayerUtils.getInstance().stringForAudioTime(0)
        mPlayIcon.setImageResource(R.mipmap.ic_player_play)
        mPlayIconLand.setImageResource(R.mipmap.ic_player_play)
    }
}