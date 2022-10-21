package org.nudt.player.ui.player

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.nudt.player.data.model.VodInfoModel.PlayUrl
import org.nudt.player.data.model.VodInfoModel

class PlayerViewModel :ViewModel() {
    val vodInfo = MutableLiveData<VodInfoModel>()
    val currentPlayUrl = MutableLiveData<PlayUrl>()

    fun setVodInfo(vod: VodInfoModel) {
        vodInfo.postValue(vod)
    }

    fun setPlayUrl(playUrl: PlayUrl) {
        currentPlayUrl.value = playUrl
    }
}