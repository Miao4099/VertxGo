package com.runyu.wechat.msg.std

import com.runyu.wechat.WxConst


/**
 * Created by sheng on 2017/10/24.
 */
class WxMsgVoice(toUser:String, fromUser:String): WxMsg(toUser, fromUser,"voice"){
    override fun getCDataFields():Set<String>{
        return super.getCDataFields().plus(WxConst.FieldName.FORMAT)
    }


    constructor(toUser:String, fromUser:String, mediaId:String, msgId:String,format:String="amr") : this(toUser, fromUser) {
        mJson.put(WxConst.FieldName.MSG_ID,msgId)
        mJson.put(WxConst.FieldName.MEDIA_ID,mediaId)
        mJson.put(WxConst.FieldName.FORMAT,format)
    }
}