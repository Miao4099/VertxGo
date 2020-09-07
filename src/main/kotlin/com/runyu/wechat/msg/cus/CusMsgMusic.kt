package com.runyu.wechat.msg.cus



/**
 * Created by sheng on 2017/11/7.
 */
class CusMsgMusic(toUser:String): CusMsg(toUser,"music"){
    constructor(toUser:String, title:String,description:String,musicurl:String,hqmusicurl:String="",thumb_media_id:String="") : this(toUser) {
        var j=io.vertx.core.json.JsonObject()
        j.put("thumb_media_id".toLowerCase(),thumb_media_id)
        j.put("title".toLowerCase(),title)
        j.put("description".toLowerCase(),description)
        j.put("musicurl".toLowerCase(),musicurl)
        j.put("hqmusicurl".toLowerCase(),hqmusicurl)
        put("music",j)
    }

}