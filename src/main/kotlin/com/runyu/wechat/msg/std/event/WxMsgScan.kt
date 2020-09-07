package com.runyu.wechat.msg.std.event

import com.runyu.wechat.WxConst


/**
 * Created by sheng on 2017/10/24.
 */
class WxMsgScan(toUser:String, fromUser:String): WxMsgEvent(toUser,fromUser,"SCAN"){
    override fun getCDataFields():Set<String>{
        return super.getCDataFields().plus(WxConst.FieldName.EVENT_KEY)
                .plus(WxConst.FieldName.TICKET)
    }

    constructor(toUser:String, fromUser:String, key:String,ticket:String) : this(toUser,fromUser) {
        mJson.put(WxConst.FieldName.EVENT_KEY,key)
        mJson.put(WxConst.FieldName.TICKET,ticket)
    }
}