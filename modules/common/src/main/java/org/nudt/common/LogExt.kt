package org.nudt.common

import android.util.Log

var LOG_ENABLE = true
var SHOW_CLASS_NAME = true

const val LOG_TAG = "videoDowner"

/**
 * 打印普通日志
 */
fun <T> T.log(prefix: String = ""): T {
    var prefixStr = if (prefix.isEmpty()) " --> " else " --> [$prefix] "
    if (SHOW_CLASS_NAME) {
        val traceElement = Thread.currentThread().stackTrace[4]
        prefixStr = traceElement.getPrefix() + prefixStr
        //Log.i(LOG_TAG, "sElements[4] = " + Thread.currentThread().stackTrace[4].toString()) //DishLog.x上层调用者
    }
    if (LOG_ENABLE) {
        when (this) {
            is Throwable -> Log.e(LOG_TAG, prefixStr + this.message, this)
            is Collection<*> -> {
                Log.d(LOG_TAG, "$prefixStr [")
                for (obj in this) {
                    Log.d(LOG_TAG, "    " + obj.toString() + ",")
                }
                Log.d(LOG_TAG, "]")
            }
            else -> Log.d(LOG_TAG, prefixStr + toString())
        }
    }
    return this
}

fun StackTraceElement.getPrefix(): String {
    if (isNativeMethod) {
        return "(Native Method)"
    } else if (fileName != null) {
        return if (lineNumber >= 0) {
            "($fileName:$lineNumber)"
        } else {
            "($fileName)"
        }
    } else {
        return if (lineNumber >= 0) {
            // The line number is actually the dex pc.
            "(Unknown Source:$lineNumber)"
        } else {
            "(Unknown Source)"
        }
    }
}

