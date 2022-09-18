package org.nudt.player.ui.download

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jeffmony.downloader.model.VideoTaskItem


class VideoTaskViewModel : ViewModel() {

    val videoTaskItems: MutableLiveData<List<VideoTaskItem>> = MutableLiveData()

    fun updateVideoTaskItems(items: List<VideoTaskItem>) {
        videoTaskItems.postValue(items)
    }

    fun updateVideoTaskItem(item: VideoTaskItem) {
        val currentList = videoTaskItems.value
        if (currentList != null) {
            val updatedList = currentList.toMutableList()
            for (index in 0 until updatedList.size) {
                if (updatedList[index].url == item.url) updatedList[index] = item
            }
            videoTaskItems.postValue(updatedList)
        }
    }

    fun addVideoTaskItem(item: VideoTaskItem) {
        val currentList = videoTaskItems.value
        if (currentList == null) {
            videoTaskItems.postValue(listOf(item))
        } else {
            val updatedList = currentList.toMutableList()
            updatedList.add(0, item)
            videoTaskItems.postValue(updatedList)
        }
    }

    fun removeVideoTaskItem(item: VideoTaskItem) {
        val currentList = videoTaskItems.value
        if (currentList != null) {
            val updatedList = currentList.toMutableList()
            updatedList.remove(item)
            videoTaskItems.postValue(updatedList)
        }
    }


}