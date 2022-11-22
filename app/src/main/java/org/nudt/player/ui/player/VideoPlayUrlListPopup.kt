package org.nudt.player.ui.player

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lxj.xpopup.core.BottomPopupView
import org.nudt.player.R
import org.nudt.player.adapter.PlayUrlAdapter
import org.nudt.player.data.model.VodInfoModel

class VideoPlayUrlListPopup(context: Context) : BottomPopupView(context) {
    private lateinit var vodInfoModel: VodInfoModel
    private lateinit var playUrlAdapter: PlayUrlAdapter
    private var popupMaxHeight: Int = 0

    override fun getImplLayoutId(): Int {
        return R.layout.popup_play_url_list
    }

    override fun onCreate() {
        super.onCreate()
        vodInfoModel.apply {
            // 分集数标题
            val tvVodAllTitle = findViewById<TextView>(R.id.tv_vod_all_title)
            tvVodAllTitle.text = "分集(${playUrlList.size})"

            // 选择分集按钮列表
            val rvVodListAll = findViewById<RecyclerView>(R.id.rv_vod_list_all)
            val gridLayoutManager = GridLayoutManager(context, 6)
            rvVodListAll.layoutManager = gridLayoutManager
            //binding.rvVodListAll.addItemDecoration(GridItemDecoration())
            rvVodListAll.adapter = playUrlAdapter

            // 点击隐藏弹窗
            val ivVodAllClose = findViewById<ImageView>(R.id.iv_vod_all_close)
            ivVodAllClose.setOnClickListener {
                dismiss()
            }
        }
    }


    override fun getMaxHeight(): Int {
        return popupMaxHeight
    }

    /**
     * 传入popup初始化数据
     * @param vodInfoModel 视频数据模型
     * @param adapter 视频分集按钮列表的adapter
     * @param maxHeight 弹窗最大高度
     */
    fun initPopup(vodInfoModel: VodInfoModel, adapter: PlayUrlAdapter, maxHeight: Int): BottomPopupView {
        this.vodInfoModel = vodInfoModel
        this.playUrlAdapter = adapter
        this.popupMaxHeight = maxHeight
        return this
    }
}