package com.runyu.std


import com.runyu.std.AnyJson
import com.runyu.user.User

import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject

open class Msg(json: JsonObject? = null): AnyJson(json){
    constructor(text:String) : this() {
        super.mergeIn(JsonObject(text))
    }

    fun merge(json:JsonObject):Msg{
        super.mergeIn(json)
        return this
    }

    fun param(json:JsonObject):Msg{
        add("json",json)
        return this
    }


    fun success(c:JsonObject):Msg{
        this.merge(c).add("code",0)
        return this
    }

    fun success(key:String,value:Any):Msg{
        this.add(key,value).add("code",0)
        return this
    }

    fun content(c:JsonObject):Msg{
        this.put("data",c)
        return this
    }

    fun good(msg:String=""):Msg{
        return error(0,msg)  as Msg
    }

    fun good(c:JsonObject):Msg{
        return (error(0,Const.Prompt.GOOD_RESPONSE) as Msg).content(c)
    }


    fun bad(code:Int,msg:String):Msg{
        return error(code,msg) as Msg
    }

    fun bad(msg:String):Msg{
        return error(100,msg) as Msg
    }

    fun bad(c:JsonObject):String{
        return (Msg().error(100,Const.Prompt.BAD_RESPONSE) as Msg).content(c).get()
    }


    fun json(msg:JsonObject):Msg{
        return add("json",msg) as Msg
    }

    fun isDone():Boolean{
        return this.containsKey("is_done")&&get("is_done")
    }

    fun setDone():Msg{
        add("is_done",true)
        return this
    }

    fun notDone():Msg{
        add("is_done",false)
        return this
    }

    fun error(err: SysErr):AnyJson{
        put("code",err.code).put("message",err.msg)
        return this
    }

    fun delError():Msg{
        if(this.containsKey("code"))remove("code")
        if(this.containsKey("message"))remove("message")
        return this
    }

    fun dbBatchCmd(value:Array<String>): Msg {
        //不知道为啥要强迫转为list，原来不用的
        add(Const.MsgBody.MSG_DB_BATCH_CMD,value.toList())
        return this;
    }
    fun dbBatchCmd(): Array<String> {
        var arr=arrayOf<String>()
        for(it in (get<io.vertx.core.json.JsonArray>(Const.MsgBody.MSG_DB_BATCH_CMD))){
            arr=arr.plus(it.toString())
        }
        return arr
    }

    fun dbBatchKey(value:Array<String>): Msg {
        //不知道为啥要强迫转为list，原来不用的
        add(Const.MsgBody.MSG_DB_BATCH_KEY,value.toList())
        return this;
    }
    fun dbBatchKey(): Array<String> {
        var arr=arrayOf<String>()
        for(it in (get<io.vertx.core.json.JsonArray>(Const.MsgBody.MSG_DB_BATCH_KEY))){
            arr=arr.plus(it.toString())
        }
        return arr
    }

    fun dbCmd(value:String): Msg {
        add(Const.MsgBody.MSG_DB_CMD,value)
        return this;
    }

    fun dbCmd(): String {
        return get(Const.MsgBody.MSG_DB_CMD)
    }

    fun msgId(value:String): Msg {
        add(Const.MsgBody.MSG_ID,value)
        return this;
    }

    fun msgId(): String {
        return get(Const.MsgBody.MSG_ID)
    }

    fun userId(value:String): Msg {
        add(Const.MsgBody.MSG_USER_ID,value)
        return this;
    }

    fun userId(): String {
        return get(Const.MsgBody.MSG_USER_ID)
    }

    fun userName(value:String): Msg {
        add(Const.MsgBody.MSG_USER_NAME,value)
        return this;
    }

    fun userName(): String {
        return get(Const.MsgBody.MSG_USER_NAME)
    }

    fun userAvatar(value:String): Msg {
        add(Const.MsgBody.MSG_USER_AVATAR,value)
        return this;
    }

    fun userAvatar(): String {
        return get(Const.MsgBody.MSG_USER_AVATAR)
    }

    fun addUserFields(user: User):Msg{
        return  add("token",user.token)
                .userId(user.user_id)
                .userAvatar(user.avatar?:"")
                .userName(user.name)
    }

    override fun add(key:String,value:Any?): Msg {
        put(key,value);
        return this;
    }


    fun filters():JsonArray?{
        return jsonGet<JsonArray>("filters")
    }

    fun sorter():JsonArray?{
        return jsonGet<JsonArray>("sorter")
    }

    fun html(html:String):Msg{
        return add("html",html) as Msg
    }

    fun html():String{
        return get("html") as String
    }

}

inline fun <T> JsonObject.jsonGet(key:String,default:T):T{
    if(containsKey("json")){
        var j=getValue("json")
        if(j is String) {
            if (JsonObject(j).containsKey(key))
                return JsonObject(j).getValue(key) as T
        }
        else {
            if ((j as JsonObject).containsKey(key))
                return j.getValue(key) as T
        }
    }
    return default
}

fun <T> JsonObject.jsonGet(key:String):T?{
    return jsonGet(key,null)
}

fun <T> JsonObject.get(key:String):T?{
    return getValue(key,null) as T
}

data class PageInfo(var pageSize:Int,var pageIndex:Int)

fun JsonObject.pageInfo():PageInfo{
    return PageInfo(jsonGet("page_size",50),jsonGet("page_index",1))
}

fun JsonObject.addColumns(vararg cols: String):JsonObject{
    this.put("columns",cols.toList())
    return this
}