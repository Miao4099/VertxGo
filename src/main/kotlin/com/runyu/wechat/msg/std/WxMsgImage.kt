package com.runyu.wechat.msg.std


import com.runyu.wechat.WxConst

/**
 * Created by sheng on 2017/10/24.
 */
class WxMsgImage(toUser:String, fromUser:String): WxMsg(toUser, fromUser,"image"){
    override fun getCDataFields():Set<String>{
        return super.getCDataFields().plus(WxConst.FieldName.CONTENT)
    }


    constructor(toUser:String, fromUser:String, mediaId:String, picUrl:String="") : this(toUser, fromUser) {
        mJson.put(WxConst.FieldName.PIC_URL,picUrl)
        mJson.put(WxConst.FieldName.MEDIA_ID,mediaId)
    }


}
