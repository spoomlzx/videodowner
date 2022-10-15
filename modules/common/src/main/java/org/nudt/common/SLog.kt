package org.nudt.common

import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by qinwei on 2016/4/14 17:40
 * email:qinwei_it@163.com
 */
object SLog {
    const val TAG = "VideoDowner"
    var LOG_LEVEL = Log.DEBUG

    fun v(msg: String?) {
        println(Log.VERBOSE, msg)
    }

    fun s(): Int {
        return 1
    }

    fun d(msg: String?) {
        println(Log.DEBUG, msg)
    }

    fun i(msg: String?) {
        println(Log.INFO, msg)
    }

    fun w(msg: String?) {
        println(Log.WARN, msg)
    }

    fun e(msg: String?) {
        println(Log.ERROR, msg)
    }

    fun json(msg: String, headString: String) {
        printJson(TAG, msg, headString)
    }

    fun printThreadStackTrace() {
        //通过线程栈帧元素获取相应信息
        Log.i("DishLog", "sElements[0] = " + Thread.currentThread().stackTrace[0]) //VMStack
        Log.i("DishLog", "sElements[1] = " + Thread.currentThread().stackTrace[1]) //Thread
        Log.i("DishLog", "sElements[2] = " + Thread.currentThread().stackTrace[2]) //当前方法帧元素
        Log.i("DishLog", "sElements[3] = " + Thread.currentThread().stackTrace[3]) //DishLog.x栈元素
        Log.i("DishLog", "sElements[4] = " + Thread.currentThread().stackTrace[4]) //DishLog.x上层调用者
    }

    val LINE_SEPARATOR = System.getProperty("line.separator")
    private fun printLine(tag: String?, isTop: Boolean) {
        if (isTop) {
            Log.d(tag, "╔═══════════════════════════════════════════════════════════════════════════════════════")
        } else {
            Log.d(tag, "╚═══════════════════════════════════════════════════════════════════════════════════════")
        }
    }

    private fun printJson(tag: String?, msg: String, headString: String) {
        var message: String
        message = try {
            if (msg.startsWith("{")) {
                val jsonObject = JSONObject(msg)
                jsonObject.toString(4) //最重要的方法，就一行，返回格式化的json字符串，其中的数字4是缩进字符数
            } else if (msg.startsWith("[")) {
                val jsonArray = JSONArray(msg)
                jsonArray.toString(4)
            } else {
                msg
            }
        } catch (e: JSONException) {
            msg
        }
        printLine(tag, true)
        message = headString + LINE_SEPARATOR + message
        val lines = message.split(LINE_SEPARATOR).toTypedArray()
        for (line in lines) {
            Log.d(tag, "║ $line")
        }
        printLine(tag, false)
    }

    private fun println(level: Int, msg: String?) {
        if (LOG_LEVEL <= level) {
            msg?.let {
                Log.println(level, TAG, msg)
            }
        }
    }
}