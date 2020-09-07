package com.runyu.wechat.msg.std.event

import com.runyu.wechat.WxConst


/**
 * Created by sheng on 2017/10/24.
 */
class WxMsgView(toUser:String, fromUser:String): WxMsgEvent(toUser,fromUser,"VIEW"){
    constructor(toUser:String, fromUser:String, key:String) : this(toUser,fromUser) {
        mJson.put(WxConst.FieldName.EVENT_KEY,key)
    }
}