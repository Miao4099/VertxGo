package com.runyu.wechat.msg.std


import com.runyu.wechat.WxConst
import com.runyu.wechat.msg.cus.CusMsgVoice


/**
 * Created by sheng on 2017/10/24.
 */
class WxMsgVoiceOut(toUser:String, fromUser:String): WxMsg(toUser, fromUser,"voice"){
    override fun getCDataFields():Set<String>{
        return super.getCDataFields().plus(WxConst.FieldName.MEDIA_ID)
    }


    constructor(toUser:String, fromUser:String, mediaId:String) : this(toUser, fromUser) {
        mJson.put(WxConst.FieldName.MEDIA_ID,mediaId)
    }

    override fun toXml():String{
        var xml=makeXml(mJson)
        xml=xml.replaceFirst("<MediaId>","<Voice><MediaId>")
        xml=xml.replaceFirst("</MediaId>","</MediaId></Voice>")
        return xml
    }

    override fun toCusMsg(): CusMsgVoice?{
        return CusMsgVoice(toUser(), mediaId())
    }
}