package com.jeffmony.downloader.model

object VideoTaskState {
    const val DEFAULT = 0 //默认状态
    const val PENDING = -1 //下载排队
    const val PREPARE = 1 //下载准备中
    const val START = 2 //开始下载
    const val DOWNLOADING = 3 //下载中
    const val PROXYREADY = 4 //视频可以边下边播
    const val SUCCESS = 5 //下载完成
    const val ERROR = 6 //下载出错
    const val PAUSE = 7 //下载暂停
    const val ENOSPC = 8 //空间不足
}