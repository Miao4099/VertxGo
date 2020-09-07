package com.runyu.wechat.msg.cus

/**
 * Created by sheng on 2017/11/7.
 */
class CusMsgCard(toUser:String): CusMsg(toUser,"wxcard"){
    constructor(toUser:String, cardId:String) : this(toUser) {
        var j=io.vertx.core.json.JsonObject()
        j.put("card_id",cardId)
        put("wxcard",j)
    }
}