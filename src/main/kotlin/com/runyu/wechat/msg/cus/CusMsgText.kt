package com.runyu.wechat.msg.cus


import com.runyu.wechat.WxConst
import io.vertx.core.json.Json


/**
 * Created by sheng on 2017/11/7.
 */
class CusMsgText(toUser:String): CusMsg(toUser,"text"){
    constructor(toUser:String, content:String) : this(toUser) {
        var j=io.vertx.core.json.JsonObject()
        j.put(WxConst.FieldName.CONTENT.toLowerCase(),content)
        put("text",j)
    }


}