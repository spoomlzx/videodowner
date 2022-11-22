package org.nudt.player.ui.player

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.lxj.xpopup.core.BottomPopupView
import org.nudt.player.R
import org.nudt.player.data.model.VodInfoModel

class VideoDetailPopup(context: Context) : BottomPopupView(context) {
    private lateinit var vodInfoModel: VodInfoModel
    private var popupMaxHeight: Int = 0

    override fun getImplLayoutId(): Int {
        return R.layout.popup_video_detail
    }

    override fun onCreate() {
        super.onCreate()
        vodInfoModel.apply {
            //Glide.with(this@OnlinePlayerActivity).load(SpUtils.basePicUrl + vod_pic).into(player.posterImageView)

            val tvContent = findViewById<TextView>(R.id.tv_content)
            val tvDirector = findViewById<TextView>(R.id.tv_director)
            val tvActor = findViewById<TextView>(R.id.tv_actor)
            val tvYear = findViewById<TextView>(R.id.tv_year)
            val ivDescriptionClose = findViewById<ImageView>(R.id.iv_description_close)

            tvContent.text = vod_content
            tvDirector.text = "导演：$vod_director"
            tvActor.text = "演员：$vod_actor"
            tvYear.text = "年份：$vod_year"

            // 点击隐藏弹窗
            ivDescriptionClose.setOnClickListener {
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
     * @param maxHeight 弹窗最大高度
     */
    fun initPopup(vodInfoModel: VodInfoModel, maxHeight: Int): BottomPopupView {
        this.vodInfoModel = vodInfoModel
        this.popupMaxHeight = maxHeight
        return this
    }
}