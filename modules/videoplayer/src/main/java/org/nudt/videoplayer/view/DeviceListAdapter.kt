package org.nudt.videoplayer.view

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.qingfeng.clinglibrary.entity.ClingDevice
import com.qingfeng.clinglibrary.service.manager.ClingManager
import com.qingfeng.clinglibrary.util.Utils
import org.nudt.common.SLog
import org.nudt.videoplayer.databinding.DlanDeviceItemBinding

class DeviceListAdapter(val context: Context, private val devices: ArrayList<ClingDevice>, val onDeviceItemClickListener: OnDeviceItemClickListener?) :
    RecyclerView.Adapter<DeviceListAdapter.DeviceHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceHolder {
        return DeviceHolder(DlanDeviceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: DeviceHolder, position: Int) {
        if (!TextUtils.isEmpty(devices[position].device.details.friendlyName)) {
            holder.binding.dlanDeviceName.text = devices[position].device.details.friendlyName
            holder.binding.rlDeviceItem.setOnClickListener {
                if (onDeviceItemClickListener != null) {
                    // 选择连接设备
                    val item = devices[position]
                    val device = item.device
                    if (null == device) {
                        onDeviceItemClickListener.onDeviceItemClick(null, false)
                        return@setOnClickListener
                    }
                    ClingManager.getInstance().selectedDevice = item
                    onDeviceItemClickListener.onDeviceItemClick(devices[position], true)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    fun addDevice(device: ClingDevice?) {
        device?.let {
            devices.add(device)
            notifyDataSetChanged()
        }
    }

    fun removeDevice(device: ClingDevice?) {
        if (devices.contains(device)) {
            devices.remove(device)
            notifyDataSetChanged()
        }
    }


    interface OnDeviceItemClickListener {
        fun onDeviceItemClick(device: ClingDevice?, isActive: Boolean)
    }

    class DeviceHolder(val binding: DlanDeviceItemBinding) : RecyclerView.ViewHolder(binding.root)

}