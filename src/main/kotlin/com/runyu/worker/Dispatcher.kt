package com.runyu.worker

import com.runyu.std.AnyJson
import com.runyu.std.Log
import com.runyu.std.Msg
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.eventbus.Message


/**
 * Created by sheng on 2017/6/1.
 */


abstract class Dispatcher{
    protected var mMode=0//0：找不到handler就报错 1：找不到就原样返回
    protected var mAgents:HashMap<String,(key: String, msg: JsonObject,message: Message<JsonObject>)-> Msg>?=null;

    init{
        mAgents=HashMap<String,(key: String, msg:JsonObject,message: Message<JsonObject>)-> Msg>()
    }

    fun add(key:String,l:(key: String, msg: JsonObject,message: Message<JsonObject>)-> Msg): Dispatcher {
        mAgents?.put(key,l);
        return this;
    }

    fun call(msg:JsonObject,message: Message<JsonObject>): Msg {
        val msg_key:String?= key(msg)
        if(null!=msg_key) {
            try {
                if(mAgents!!.containsKey(msg_key))
                    return mAgents?.get(msg_key)?.invoke(msg_key!!, msg, message)!!
                else {
                    if(0==mMode)
                        return Msg().error(10010, "no handler found for $msg_key") as Msg//找不到就反馈
                }
            } catch (e: KotlinNullPointerException) {
                e.printStackTrace()
                Log.e("MsgIdDispatcher", "can't find handler for $msg_key for $msg")
            }
            return Msg(msg)
        }else
            return Msg(msg)
    }

    abstract fun key(msg: JsonObject):String?
}