package org.nudt.videoplayer.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.ServiceConnection
import android.view.LayoutInflater
import com.lxj.xpopup.core.CenterPopupView

import org.nudt.videoplayer.R
import org.nudt.videoplayer.databinding.DlanDeviceListPopupLayoutBinding

@SuppressLint("ViewConstructor")
class DlanListPopupView(context: Context, val url: String, val title: String) : CenterPopupView(context) {

    private val binding by lazy { DlanDeviceListPopupLayoutBinding.inflate(LayoutInflater.from(context)) }

    private val mUpnpServiceConnection: ServiceConnection? = null

    override fun onCreate() {
        super.onCreate()

        binding.dlanToCancel.setOnClickListener {
            dismiss()
            if (mUpnpServiceConnection != null) {
                context.unbindService(mUpnpServiceConnection)
            }
        }



    }

    override fun getImplLayoutId(): Int {
        return R.layout.dlan_device_list_popup_layout
    }

    override fun dismiss() {
        super.dismiss()
        if (mUpnpServiceConnection != null) {
            context.unbindService(mUpnpServiceConnection)
        }
    }

}