package com.runyu.wechat.msg.cus



/**
 * Created by sheng on 2017/11/7.
 */
class CusMsgNews(toUser:String): CusMsg(toUser,"mpnews"){
    constructor(toUser:String, mediaId:String) : this(toUser) {
        var j=io.vertx.core.json.JsonObject()
        j.put("media_id".toLowerCase(),mediaId)
        put("mpnews",j)
    }
}