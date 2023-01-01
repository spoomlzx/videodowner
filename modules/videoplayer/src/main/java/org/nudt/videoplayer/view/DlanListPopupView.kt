package org.nudt.videoplayer.view

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lxj.xpopup.core.CenterPopupView
import com.qingfeng.clinglibrary.entity.ClingDevice
import com.qingfeng.clinglibrary.entity.IDevice
import com.qingfeng.clinglibrary.listener.BrowseRegistryListener
import com.qingfeng.clinglibrary.listener.DeviceListChangedListener
import com.qingfeng.clinglibrary.service.ClingUpnpService
import com.qingfeng.clinglibrary.service.manager.ClingManager
import com.qingfeng.clinglibrary.service.manager.DeviceManager
import org.nudt.videoplayer.R
import org.nudt.videoplayer.ui.VideoControlActivity
import org.nudt.videoplayer.util.log

@SuppressLint("ViewConstructor")
class DlanListPopupView(context: Context, val url: String, val title: String) : CenterPopupView(context) {

    private var mUpnpServiceConnection: ServiceConnection? = null
    private val registryListener = BrowseRegistryListener()
    private var beyondUpnpService: ClingUpnpService? = null
    private lateinit var clingUpnpServiceManager: ClingManager

    override fun onCreate() {
        super.onCreate()

        // 显示设备列表
        val deviceAdapter = DeviceListAdapter(context, ArrayList(), object : DeviceListAdapter.OnDeviceItemClickListener {
            override fun onDeviceItemClick(device: ClingDevice?, isActive: Boolean) {
                if (device != null && isActive) {
                    "video to control url: $url".log()
                    val intent = Intent(context, VideoControlActivity::class.java)
                    intent.putExtra("title", title)
                    intent.putExtra("url", url)
                    context.startActivity(intent)
                    dismiss()
                } else {
                    Toast.makeText(context, "未连接到设备", Toast.LENGTH_SHORT).show()
                }
            }
        })
        val deviceList = findViewById<RecyclerView>(R.id.device_list)
        deviceList.layoutManager = LinearLayoutManager(context)
        deviceList.adapter = deviceAdapter

        // 取消，关闭wlan页面
        val cancel = findViewById<TextView>(R.id.dlan_to_cancel)
        cancel.setOnClickListener {
            dismiss()
            if (mUpnpServiceConnection != null) {
                context.unbindService(mUpnpServiceConnection as ServiceConnection)
            }
        }


        initAndRefresh()

        registryListener.setOnDeviceListChangedListener(object : DeviceListChangedListener {
            override fun onDeviceAdded(device: IDevice<*>?) {
                deviceList.post { deviceAdapter.addDevice(device as ClingDevice?) }
            }

            override fun onDeviceRemoved(device: IDevice<*>?) {
                deviceList.post { deviceAdapter.removeDevice(device as ClingDevice?) }
            }
        })

    }



    private fun initAndRefresh() {

        mUpnpServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(className: ComponentName, service: IBinder) {
                val binder = service as ClingUpnpService.LocalBinder
                beyondUpnpService = binder.service
                clingUpnpServiceManager = ClingManager.getInstance()
                clingUpnpServiceManager.setUpnpService(beyondUpnpService)
                clingUpnpServiceManager.setDeviceManager(DeviceManager())
                clingUpnpServiceManager.registry.addListener(registryListener)
                //Search on service created.
                clingUpnpServiceManager.searchDevices()
            }

            override fun onServiceDisconnected(className: ComponentName) {
                mUpnpServiceConnection = null
            }
        }

        val upnpServiceIntent = Intent(context, ClingUpnpService::class.java)
        context.bindService(upnpServiceIntent, mUpnpServiceConnection as ServiceConnection, Context.BIND_AUTO_CREATE)


    }

    override fun getImplLayoutId(): Int {
        return R.layout.dlan_device_list_popup_layout
    }

    override fun dismiss() {
        super.dismiss()
    }

}