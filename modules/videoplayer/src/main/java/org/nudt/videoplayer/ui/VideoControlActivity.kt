package org.nudt.videoplayer.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.qingfeng.clinglibrary.control.ClingPlayControl
import com.qingfeng.clinglibrary.control.callback.ControlCallback
import com.qingfeng.clinglibrary.entity.IResponse
import com.qingfeng.clinglibrary.service.manager.ClingManager
import org.nudt.common.SLog
import org.nudt.videoplayer.databinding.ActivityVideoControlBinding

class VideoControlActivity : AppCompatActivity() {

    private val mClingPlayControl = ClingPlayControl() //投屏控制器


    private val binding by lazy { ActivityVideoControlBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val title = intent.getStringExtra("title")
        val url = intent.getStringExtra("url")
        binding.tvTitle.text = title
        binding.tvUrl.text = url
        initPlay(title, url)

        //binding.tvTitle.text = ClingManager.getInstance().selectedDevice.device.toString()


        setContentView(binding.root)
    }

    private fun initPlay(title: String?, url: String?) {
        mClingPlayControl.playNew(url, object : ControlCallback<Any?> {
            override fun success(response: IResponse<Any?>?) {
                ClingManager.getInstance().registerAVTransport(this@VideoControlActivity)
                ClingManager.getInstance().registerRenderingControl(this@VideoControlActivity)
                binding.tvTitle.text = "正在播放"
            }

            override fun fail(response: IResponse<Any?>?) {
                if (response != null) {
                    SLog.d("response: ${response.response.toString()}")
                }
            }
        })
    }
}