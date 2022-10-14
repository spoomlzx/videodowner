package com.jeffmony.downloader

import java.lang.Exception

class VideoDownloadException : Exception {
    var msg: String? = null
        private set

    constructor(message: String?) : super(message) {
        msg = message
    }

    constructor(message: String?, cause: Throwable?) : super(message, cause) {
        msg = message
    }

    constructor(cause: Throwable?) : super(cause) {}
}