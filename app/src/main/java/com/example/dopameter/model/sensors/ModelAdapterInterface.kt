package com.example.dopameter.model.sensors

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap

interface ModelInterface {
    open var time: Long?
    //        TODO(fix order of columns)
    fun getMembers(): Array<String> {
        var res = ArrayList<String>()
        for (field in this::class.java.declaredFields) {
            res.add(field.name)
        }
        return res.toTypedArray()
    }
    fun getFileStringArray(): Array<String> {
        var res = ArrayList<String>()
        for (field in this::class.java.declaredFields) {
            field.isAccessible = true
            res.add(field?.get(this).toString())

        }
        return res.toTypedArray()
    }
    companion object {
        val gson = Gson()
    }
    fun toHashMap(): HashMap<String, Any> {
        val json = gson.toJson(this)
        var res: HashMap<String, Any> = gson.fromJson(json, object: TypeToken<LinkedHashMap<String, Any>>() {}.type)
        return res
    }
    fun toJson(): String {
        return gson.toJson(this)
    }
}

interface ModelAdapterInterface<T: ModelInterface> {
    var queue: Queue<T>
    var mModelInterface: Class<T>
    fun add(data: T) {
        queue.add(data)
    }
    fun clear() {
        queue.clear()
    }
    fun toHashMap(): HashMap<String, Any> {
        var res = hashMapOf<String, Any>()
        for (item in queue) {
            res.putAll(item.toHashMap())
        }
        return res
    }
    fun toJsonString(): String {
        var res = arrayListOf<String>()
        queue.forEachIndexed{i, item ->
            res.add(item.toJson())
        }
        return res.joinToString()
    }

}