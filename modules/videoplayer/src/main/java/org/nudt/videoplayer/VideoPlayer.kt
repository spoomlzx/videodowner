package org.nudt.videoplayer

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.widget.Toast
import com.android.iplayer.base.AbstractMediaPlayer
import com.android.iplayer.base.BasePlayer
import com.android.iplayer.controller.VideoController
import com.android.iplayer.interfaces.IVideoController
import com.android.iplayer.listener.OnPlayerEventListener
import com.android.iplayer.media.IMediaPlayer
import com.lxj.xpopup.XPopup
import org.nudt.videoplayer.controls.*
import org.nudt.videoplayer.media.ExoPlayerFactory
import org.nudt.videoplayer.view.DlanListPopupView

class VideoPlayer(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : BasePlayer(context, attrs, defStyleAttr) {
    constructor(context: Context?) : this(context, null) {}
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0) {}

    private lateinit var controller: VideoController
    private lateinit var toolBarView: ControlToolBarView

    private lateinit var title: String
    private lateinit var url: String

    override fun initViews() {
        controller = VideoController(context)



        setController(controller)
        //给播放器控制器绑定自定义UI交互组件，也可调用initControlComponents()一键使用SDK内部提供的所有UI交互组件
        toolBarView = ControlToolBarView(context) //标题栏，返回按钮、视频标题、功能按钮、系统时间、电池电量等组件
        toolBarView.target = IVideoController.TARGET_CONTROL_TOOL
        toolBarView.showBack(false) //是否显示返回按钮,仅限竖屏情况下，横屏模式下强制显示
        //监听标题栏的功能事件
        toolBarView.setOnToolBarActionListener(object : ControlToolBarView.OnToolBarActionListener() {
            override fun onTv() {
                if (url.isEmpty()) {
                    Toast.makeText(context, "视频地址为空", Toast.LENGTH_SHORT).show()
                    return
                }
                if (url.startsWith("file")) {
                    Toast.makeText(context, "暂不支持投屏本地视频", Toast.LENGTH_SHORT).show()
                    return
                }
                // 如果是播放地址，则弹出设备列表框
                if (url.startsWith("https://") || url.startsWith("http://")) {
                    val dlanListPop = DlanListPopupView(context, url, title)
                    XPopup.Builder(context).asCustom(dlanListPop).show()
                }
            }

            override fun onWindow() {
                //XLog.d("onWindow")
            }

            override fun onMenu() {
                //XLog.d("onMenu")
            }
        })

        val functionBarView = ControlFunctionBarView(context) //底部时间、seek、静音、全屏功能栏
        val gestureView = ControlGestureView(context) //手势控制屏幕亮度、系统音量、快进、快退UI交互
        val completionView = ControlCompletionView(context) //播放完成、重试
        val statusView = ControlStatusView(context) //移动网络播放提示、播放失败、试看完成
        val loadingView = ControlLoadingView(context) //加载中、开始播放
        val windowView = ControWindowView(context) //悬浮窗窗口播放器的窗口样式
        controller.addControllerWidget(toolBarView, functionBarView, gestureView, completionView, statusView, loadingView, windowView)

        setOnPlayerActionListener(object : OnPlayerEventListener() {
            //自定义解码器
            override fun createMediaPlayer(): AbstractMediaPlayer {
                return ExoPlayerFactory.create().createPlayer(context) //IJK解码器
            }
        })

        //setMobileNetwork(true)

        setLoop(false) //是否循环播放
        setZoomModel(IMediaPlayer.MODE_ZOOM_TO_FIT) //设置视频画面渲染模式为：全屏裁剪缩放模式
        //mVideoPlayer.setLandscapeWindowTranslucent(true);//全屏模式下是否启用沉浸样式，默认关闭。辅以setZoomModel为IMediaPlayer.MODE_ZOOM_CROPPING效果最佳
        setProgressCallBackSpaceMilliss(300) //设置进度条回调间隔时间(毫秒)
        setSpeed(1.0f) //设置播放倍速(默认正常即1.0f，区间：0.5f-2.0f)
        setMirror(false) //是否镜像显示
        setAutoChangeOrientation(true) //是否开启重力旋转。当系统"自动旋转"开启+正在播放生效
    }

    fun setTitle(title: String) {
        toolBarView.setTitle(title)
        this.title = title
    }

    fun setPlayUrl(url: String) {
        setDataSource(url)
        this.url = url
    }

    fun setLocalDataSource(dataSource: String) {
        val filePath = Uri.parse("file://$dataSource").toString()
        setDataSource(filePath)
    }
}