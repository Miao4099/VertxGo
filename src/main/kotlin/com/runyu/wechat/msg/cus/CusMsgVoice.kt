package com.runyu.wechat.msg.cus



/**
 * Created by sheng on 2017/11/7.
 */
class CusMsgVoice(toUser:String): CusMsg(toUser,"voice"){
    constructor(toUser:String, mediaId:String) : this(toUser) {
        var j=io.vertx.core.json.JsonObject()
        j.put("media_id",mediaId)
        put("voice",j)
    }
}