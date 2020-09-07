package com.runyu.wechat.msg.std.event

import com.runyu.wechat.WxConst
import com.runyu.wechat.msg.std.WxMsg


/**
 * Created by sheng on 2017/10/24.
 */
open class WxMsgEvent(toUser:String, fromUser:String): WxMsg(toUser,fromUser,"event"){
    override fun getCDataFields():Set<String>{
        return super.getCDataFields().plus(WxConst.FieldName.EVENT)
    }


    constructor(toUser:String, fromUser:String, event:String) : this(toUser,fromUser) {
        mJson.put(WxConst.FieldName.EVENT,event)
    }
}