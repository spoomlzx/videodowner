package zlc.season.downloadx.core

import java.util.concurrent.ConcurrentHashMap

interface TaskManager {
    fun add(task: DownloadTask): DownloadTask

    fun remove(task: DownloadTask)
}

object DefaultTaskManager : TaskManager {
    private val taskMap = ConcurrentHashMap<String, DownloadTask>()

    override fun add(task: DownloadTask): DownloadTask {
        if (taskMap[task.param.tag()] == null) {
            taskMap[task.param.tag()] = task
        } else {
            val mappedTask = taskMap[task.param.tag()]!!
            if (mappedTask.coroutineScope != task.coroutineScope) {
                // lifecycleScope 销毁以后再次创建，会是不同对象，导致无法继续下载任务
                taskMap[task.param.tag()] = task
            }
        }
        return taskMap[task.param.tag()]!!
    }

    override fun remove(task: DownloadTask) {
        taskMap.remove(task.param.tag())
    }
}