package org.nudt.player.utils

import com.tencent.mmkv.MMKV
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Preference<T>(private val key: String, private val default: T):ReadWriteProperty<Any?,T> {
    private val mMkv by lazy {
        MMKV.defaultMMKV()
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return  when(default){
            is Long -> mMkv.decodeLong(key,default)
            is Int -> mMkv.decodeInt(key,default)
            is Double -> mMkv.decodeDouble(key,default)
            is Boolean -> mMkv.decodeBool(key,default)
            is Float -> mMkv.decodeFloat(key,default)
            is String -> mMkv.decodeString(key,default)
            is ByteArray -> mMkv.decodeBytes(key,default)
            else -> throw IllegalArgumentException("Unsupported type")
        } as T
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        mMkv.let {
            when(value){
                is String -> it.encode(key, value)
                is Float -> it.encode(key, value)
                is Boolean -> it.encode(key, value)
                is Int -> it.encode(key, value)
                is Long -> it.encode(key, value)
                is Double -> it.encode(key, value)
                is ByteArray -> it.encode(key, value)
                is Nothing -> return@let
            }
        }
    }

    fun removeKey() = mMkv.removeValueForKey(key)

    fun cleanAllMMKV() = mMkv.clearAll()
}