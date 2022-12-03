package zlc.season.downloadx

import kotlinx.coroutines.CoroutineScope
import zlc.season.downloadx.core.DownloadConfig
import zlc.season.downloadx.core.DownloadParam
import zlc.season.downloadx.core.DownloadTask
import zlc.season.downloadx.helper.Default
import zlc.season.downloadx.utils.log

fun CoroutineScope.download(
    url: String,
    saveName: String = "",
    savePath: String = Default.DEFAULT_SAVE_PATH,
    downloadConfig: DownloadConfig = DownloadConfig()
): DownloadTask {
    val downloadParam = DownloadParam(url, saveName, savePath)
    "$savePath  and  $saveName".log("start to download")
    val stateHolder = DownloadTask.StateHolder()
    stateHolder.updateState(stateHolder.downloading, Progress(1200, 2344))


    val task = DownloadTask(this, downloadParam, downloadConfig, stateHolder)
    return downloadConfig.taskManager.add(task)
}

fun CoroutineScope.download(
    downloadParam: DownloadParam,
    downloadConfig: DownloadConfig = DownloadConfig()
): DownloadTask {
    val task = DownloadTask(this, downloadParam, downloadConfig)
    return downloadConfig.taskManager.add(task)
}