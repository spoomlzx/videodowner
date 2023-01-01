package org.nudt.videoplayer.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.qingfeng.clinglibrary.Intents
import com.qingfeng.clinglibrary.control.ClingPlayControl
import com.qingfeng.clinglibrary.control.callback.ControlCallback
import com.qingfeng.clinglibrary.control.callback.ControlReceiveCallback
import com.qingfeng.clinglibrary.entity.ClingVolumeResponse
import com.qingfeng.clinglibrary.entity.DLANPlayState
import com.qingfeng.clinglibrary.entity.IResponse
import com.qingfeng.clinglibrary.service.manager.ClingManager
import org.nudt.videoplayer.databinding.ActivityVideoControlBinding
import org.nudt.videoplayer.util.log

class VideoControlActivity : AppCompatActivity() {
    private val TAG = "VideoControlActivity"
    private lateinit var mContext: Context

    private val mClingPlayControl = ClingPlayControl() //投屏控制器
    private val mHandler: Handler = InnerHandler()

    private val binding by lazy { ActivityVideoControlBinding.inflate(layoutInflater) }


    private var currentVolume: Int = 0
    private var isPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this@VideoControlActivity

        initListener()
        registerReceivers()

        val title = intent.getStringExtra("title")
        val url = intent.getStringExtra("url")
        binding.tvTitle.text = title
        binding.tvUrl.text = url
        initPlay(title, url)

        //binding.tvTitle.text = ClingManager.getInstance().selectedDevice.device.toString()


        setContentView(binding.root)
    }

    private fun initPlay(title: String?, url: String?) {
        binding.tvTitle.text = title
        mClingPlayControl.playNew(url, object : ControlCallback<Any?> {
            override fun success(response: IResponse<Any?>?) {
                ClingManager.getInstance().registerAVTransport(mContext)
                ClingManager.getInstance().registerRenderingControl(mContext)
                isPlaying = true
                binding.btnPlay.text = "暂停"
            }

            override fun fail(response: IResponse<Any?>?) {
                mHandler.sendEmptyMessage(ERROR_ACTION)
            }
        })
    }

    private fun initListener() {
        getVolume()
        binding.btnVolumeUp.setOnClickListener {
            if (currentVolume <= 96) {
                currentVolume += 4
                mClingPlayControl.setVolume(currentVolume, object : ControlCallback<Any?> {
                    override fun success(res: IResponse<Any?>?) {
                        if (res != null) {
                            "volume up success: ${res.response}".log()
                        }
                    }

                    override fun fail(res: IResponse<Any?>?) {
                        "volume up failure: $res".log()
                    }
                })
            }
        }
        binding.btnVolumeDown.setOnClickListener {
            if (currentVolume <= 4) {
                currentVolume = 0
            } else {
                currentVolume -= 4
            }
            mClingPlayControl.setVolume(currentVolume, object : ControlCallback<Any?> {
                override fun success(res: IResponse<Any?>?) {
                    "volume up success: $res".log()
                }

                override fun fail(res: IResponse<Any?>?) {
                    "volume up failure: $res".log()
                }
            })
        }
        binding.btnPlay.setOnClickListener {
            if (isPlaying) {
                pause()
            } else {
                continuePlay()
            }
        }
    }


    private fun pause() {
        mClingPlayControl.pause(object : ControlCallback<Any?> {
            override fun success(response: IResponse<Any?>?) {
                isPlaying = false
                binding.btnPlay.text = "播放"
                if (response != null) {
                    "pause: ${response.response}".log()
                }
            }

            override fun fail(response: IResponse<Any?>?) {}
        })
    }

    private fun continuePlay() {
        mClingPlayControl.play(object : ControlCallback<Any?> {
            override fun success(response: IResponse<Any?>?) {
                isPlaying = true
                binding.btnPlay.text = "暂停"
                if (response != null) {
                    "continue play: ${response.response}".log()
                }
            }

            override fun fail(response: IResponse<Any?>?) {}
        })
    }

    /**
     * 获取当前音量
     * todo 这里应该是获取手机的音量
     */
    private fun getVolume() {
        mClingPlayControl.getVolume(object : ControlReceiveCallback {
            override fun receive(response: IResponse<*>) {
                if (response is ClingVolumeResponse) {
                    currentVolume = response.response
                    binding.tvVolume.text = "音量：${response.response}"
                }

                "get volume: $response".log()
            }

            override fun success(response: IResponse<*>?) {
                if (response != null) {
                    "get volume success: ${response.response}".log()
                }
            }

            override fun fail(response: IResponse<*>?) {}
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        // 注释以下代码，这样在推出视频播放时，投屏的设备不会退出该视频
        //stop()

//        mHandler.removeCallbacksAndMessages(null)
//        unregisterReceiver(transportStateBroadcastReceiver)
//        ClingManager.getInstance().destroy()
//        ClingDeviceList.getInstance().destroy()
    }

    /**
     * 停止
     */
    private fun stop() {
        mClingPlayControl.stop(object : ControlCallback<Any?> {
            override fun success(response: IResponse<Any?>?) {
                TODO("Not yet implemented")
            }

            override fun fail(response: IResponse<Any?>?) {
                TODO("Not yet implemented")
            }
        })
    }


    /**
     * 连接设备状态: 播放状态
     */
    val PLAY_ACTION = 0xa1

    /**
     * 连接设备状态: 暂停状态
     */
    val PAUSE_ACTION = 0xa2

    /**
     * 连接设备状态: 停止状态
     */
    val STOP_ACTION = 0xa3

    /**
     * 连接设备状态: 转菊花状态
     */
    val TRANSITIONING_ACTION = 0xa4

    /**
     * 获取进度
     */
    val EXTRA_POSITION = 0xa5

    /**
     * 投放失败
     */
    val ERROR_ACTION = 0xa6

    /**
     * tv端播放完成
     */
    val ACTION_PLAY_COMPLETE = 0xa7

    val ACTION_POSITION_CALLBACK = 0xa8

    private fun registerReceivers() {
        //Register play status broadcast
        val filter = IntentFilter()
        filter.addAction(Intents.ACTION_PLAYING)
        filter.addAction(Intents.ACTION_PAUSED_PLAYBACK)
        filter.addAction(Intents.ACTION_STOPPED)
        filter.addAction(Intents.ACTION_TRANSITIONING)
        filter.addAction(Intents.ACTION_POSITION_CALLBACK)
        filter.addAction(Intents.ACTION_PLAY_COMPLETE)
        registerReceiver(transportStateBroadcastReceiver, filter)
    }

    inner class InnerHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                PLAY_ACTION -> {
                    Log.i(TAG, "Execute PLAY_ACTION")
                    Toast.makeText(mContext, "正在投放", Toast.LENGTH_SHORT).show()
                    //startGetProgress()
                    mClingPlayControl.currentState = DLANPlayState.PLAY
                }
                PAUSE_ACTION -> {
                    Log.i(TAG, "Execute PAUSE_ACTION")
                    mClingPlayControl.currentState = DLANPlayState.PAUSE
                }
                STOP_ACTION -> {
                    Log.i(TAG, "Execute STOP_ACTION")
                    mClingPlayControl.currentState = DLANPlayState.STOP
                }
                TRANSITIONING_ACTION -> {
                    Log.i(TAG, "Execute TRANSITIONING_ACTION")
                    Toast.makeText(mContext, "正在连接", Toast.LENGTH_SHORT).show()
                }
                ACTION_POSITION_CALLBACK -> {}
                ACTION_PLAY_COMPLETE -> Log.i(TAG, "Execute GET_POSITION_INFO_ACTION")
                ERROR_ACTION -> {
                    Log.e(TAG, "Execute ERROR_ACTION")
                    Toast.makeText(mContext, "投放失败", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * 接收状态改变信息
     */
    private var transportStateBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            Log.e(TAG, "Receive playback intent:$action")
            if (Intents.ACTION_PLAYING == action) {
                mHandler.sendEmptyMessage(PLAY_ACTION)
            } else if (Intents.ACTION_PAUSED_PLAYBACK == action) {
                mHandler.sendEmptyMessage(PAUSE_ACTION)
            } else if (Intents.ACTION_STOPPED == action) {
                mHandler.sendEmptyMessage(STOP_ACTION)
            } else if (Intents.ACTION_TRANSITIONING == action) {
                mHandler.sendEmptyMessage(TRANSITIONING_ACTION)
            } else if (Intents.ACTION_POSITION_CALLBACK == action) {
                val msg = Message.obtain()
                msg.what = ACTION_POSITION_CALLBACK
                msg.arg1 = intent.getIntExtra(Intents.EXTRA_POSITION, -1)
                mHandler.sendMessage(msg)
            } else if (Intents.ACTION_PLAY_COMPLETE == action) {
                mHandler.sendEmptyMessage(ACTION_PLAY_COMPLETE)
            }
        }
    }
}