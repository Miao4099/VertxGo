package com.runyu.wechat.msg.std


import com.runyu.wechat.WxConst
import com.runyu.wechat.msg.cus.CusMsgText


/**
 * Created by sheng on 2017/10/24.
 */
class WxMsgText(toUser:String,fromUser:String): WxMsg(toUser,fromUser,"text"){
    override fun getCDataFields():Set<String>{
        return super.getCDataFields().plus(WxConst.FieldName.CONTENT)
    }

    constructor(toUser:String, fromUser:String, content:String) : this(toUser, fromUser) {
        mJson.put(WxConst.FieldName.CONTENT,content)
    }

    override fun toCusMsg(): CusMsgText?{
        return CusMsgText(toUser(), content())
    }
}