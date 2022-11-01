package org.nudt.player.ui.player

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import org.nudt.common.SLog
import org.nudt.player.data.api.doFailure
import org.nudt.player.data.api.doSuccess
import org.nudt.player.data.db.VideoDb
import org.nudt.player.data.model.PlayHistory
import org.nudt.player.data.model.VodInfoModel
import org.nudt.player.data.model.VodInfoModel.PlayUrl
import org.nudt.player.data.repository.VideoRepository

class PlayerViewModel(private val videoRepository: VideoRepository, private val db: VideoDb) : ViewModel() {
    val vodInfo = MutableLiveData<VodInfoModel>()
    val currentIndex = MutableLiveData(0)

    val history = MutableLiveData<PlayHistory>()

    fun setCurrent(newIndex: Int) {
        currentIndex.postValue(newIndex)
    }


    fun fetchVideoInfo(vodId: Int) = liveData<VodInfoModel> {
        videoRepository.fetchVideoInfo(vodId).collectLatest { result ->
            result.doSuccess { value ->
                vodInfo.postValue(value)
                emit(value)
            }
            result.doFailure { throwable ->
                SLog.e("error: $throwable")
            }
        }
    }

    /**
     * 同步获取播放历史，会早于获取视频信息
     */
    fun fetchProgress(vodId: Int) {
        val playHistoryDao = db.playHistoryDao()
        val playHistory = playHistoryDao.getHistoryById(vodId)
        if (playHistory != null) {
            history.value = playHistory
            currentIndex.value = playHistory.vod_index
            SLog.d("get progress: $playHistory")
        }
    }

    fun savePlayHistory(duration: Long, progress: Long) {
        val playHistoryDao = db.playHistoryDao()
        vodInfo.value?.apply {
            val history = PlayHistory(
                vod_id = vod_id, vod_name = vod_name, vod_pic = vod_pic, vod_remarks = vod_remarks,
                vod_index = currentIndex.value ?: 0, progress_time = progress, total_duration = duration, last_play_time = System.currentTimeMillis()
            )
//            GlobalScope.launch {
//                playHistoryDao.insert(history)
//            }
            viewModelScope.launch {
                //SLog.d("save history")
                playHistoryDao.insert(history)
            }

        }
    }
}