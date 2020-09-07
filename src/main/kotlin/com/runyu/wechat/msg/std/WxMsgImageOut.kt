package com.runyu.wechat.msg.std


import com.runyu.wechat.WxConst
import com.runyu.wechat.msg.cus.CusMsgImage

/**
 * Created by sheng on 2017/10/24.
 */
class WxMsgImageOut(toUser:String, fromUser:String): WxMsg(toUser, fromUser,"voice"){
    override fun getCDataFields():Set<String>{
        return super.getCDataFields().plus(WxConst.FieldName.MEDIA_ID)
    }


    constructor(toUser:String, fromUser:String, mediaId:String) : this(toUser, fromUser) {
        mJson.put(WxConst.FieldName.MEDIA_ID,mediaId)
    }

    override fun toXml():String{
        var xml=makeXml(mJson)
        xml=xml.replaceFirst("<MediaId>","<Image><MediaId>")
        xml=xml.replaceFirst("</MediaId>","</MediaId></Image>")
        return xml
    }

    override fun toCusMsg(): CusMsgImage?{
        return CusMsgImage(toUser(), mediaId())
    }
}