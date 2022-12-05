# DownloadX

基于协程打造的下载工具, 支持多线程下载和断点续传

## Basic Usage

```kotlin
// 创建下载任务
val downloadTask = DownloadXManager.download("http://192.168.3.4/01.mp4", "", "")

// 监听下载进度
downloadTask.progress()
    .onEach { binding.button.setProgress(it)  }
    .launchIn(lifecycleScope)

// 或者监听下载状态
downloadTask.state()
    .onEach { binding.button.setState(it)  }
    .launchIn(lifecycleScope)

// 开始下载
downloadTask.start()
```

## 创建任务

- 指定CoroutineScope

> lifecycleScope是androidX中的扩展，需要添加以下依赖：
> implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.5.1'

基于此，可以在任意多个页面中共享同一个下载进度和下载状态

## 进度和状态

可以为progress()方法设置更新间隔，默认是200ms更新一次，如：

```kotlin
downloadTask.progress(500) // 设置为500ms更新一次进度
    .onEach { progress ->  
        // 更新进度
        setProgress(progress)
    }
    .launchIn(lifecycleScope)
```

- 监听下载状态和进度

当需要下载状态和下载进度的时候，使用这种方式获取

```kotlin
downloadTask.state()
    .onEach { state ->  
        // 更新状态
        setState(state)
        // 更新进度
        setProgress(state.progress)
    }
    .launchIn(lifecycleScope)
```

> state有以下值：**None,Waiting,Downloading,Stopped,Failed,Succeed**

同样的，可以为state()方法设置进度更新间隔


## 启动和停止

- 开始下载

```kotlin
DownloadXManager.startDownloadTask(downloadTask: DownloadTask)
```

- 停止下载

```kotlin
DownloadXManager.pauseDownloadTask(downloadTask: DownloadTask)
```

- 删除下载

```kotlin
DownloadXManager.removeDownloadTask(downloadTask: DownloadTask)
```