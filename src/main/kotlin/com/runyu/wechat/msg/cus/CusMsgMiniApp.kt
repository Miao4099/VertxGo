package com.runyu.wechat.msg.cus

/**
 * Created by sheng on 2017/11/7.
 */
class CusMsgMiniApp(toUser:String): CusMsg(toUser,"miniprogrampage"){
    constructor(toUser:String, title:String,appId:String,page:String,mediaId:String) : this(toUser) {
        var j=io.vertx.core.json.JsonObject()
        j.put("title",title)
        j.put("appid",appId)
        j.put("pagepath",page)
        j.put("thumb_media_id",mediaId)
        put("miniprogrampage",j)
    }
}