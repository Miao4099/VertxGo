package com.runyu.map

import com.runyu.std.Log
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import java.util.*
import java.util.function.BiConsumer


class Cache(var name:String) : Map<String, Any> {
    companion object{
        var mRootMap:Redis?=null
        fun init(cfg:String) {
            mRootMap=Redis(cfg)
        }
    }


    init{
        mRootMap!!.putMap(name,mapOf(Pair("dummy","#0")))
    }

    override fun containsKey(key: String): Boolean {
        return mRootMap!!.getMap(name).containsKey(key)
    }

    inline fun getValue(value:String):Any{
        if (value.startsWith("{"))
            return JsonObject(value)
        else if (value.startsWith("[")) {
            return JsonArray(value)
        }else if (value.startsWith("#")) {
            return value.drop(1).toLong()
        }else
            return value

    }

    override fun get(key: String): Any? {
        var map= mRootMap!!.getMap(name)
        var value=map!!.get(key)
        value?.let{
            return getValue(value)
        }
        return null
    }

    fun put(key: String, value: Any ): Any? {
        var map= mRootMap!!.getMap(name)
        if(value is String)
            map!!.put(key,value)
        else if(value is JsonObject || value is JsonArray){
            map!!.put(key,value.toString())
        }else if(value is Int || value is Long){
            map!!.put(key,"#"+value)
        }else
            throw Exception("type not support")
        mRootMap!!.putMap(name,map)
        Log.e("write map--<",key,value)
        return null
    }

    override fun isEmpty(): Boolean {
        return size==0
    }

    fun remove(key: String): Any? {
        mRootMap!!.del(key)
        return null
    }

    override val entries: Set<Map.Entry<String, Any>>
        get() = mRootMap!!.getMap(name).entries
    override val keys: Set<String>
        get() = mRootMap!!.getMap(name).keys
    override val size: Int
        get() = mRootMap!!.getMap(name).size
    override val values: Collection<Any>
        get() = mRootMap!!.getMap(name).values

    override fun containsValue(value: Any): Boolean {
        return values.contains(value)
    }

    fun clear(){
        entries.drop(size)
    }

    override fun forEach(action: BiConsumer<in String, in Any>) {
        Objects.requireNonNull(action)
        for ((key, value) in entries) {
            var k: String
            var v: Any?
            try {
                k = key
                v = getValue(value as String)
            } catch (ise: IllegalStateException) { // this usually means the entry is no longer in the map.
                throw ConcurrentModificationException(ise)
            }
            action.accept(k, v)
        }
    }

}


