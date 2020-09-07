package com.runyu.wechat.msg.cus


import com.runyu.std.AnyJson
import com.runyu.wechat.WxConst
import io.vertx.core.json.JsonObject


/**
 * Created by sheng on 2017/11/7.
 */
open class CusMsg: AnyJson {
    protected lateinit var mJson:JsonObject

    constructor(json:String){
        mJson= JsonObject(json)
    }

    constructor(toUser:String,type:String){
        put("touser",toUser)
        put(WxConst.FieldName.MSG_TYPE,type)
    }

    fun set(key:String,value:Any?){
        mJson.put(key.toLowerCase(),value)
    }

    fun add(parent:String,child:String,value:String){
        var j=io.vertx.core.json.JsonObject()
        j.put(child,value)
        put(parent,j)
    }

    fun by(account:String){
        add("customservice","kf_account",account)
    }

    fun toStr():String{
        return mJson.toString()
    }
}