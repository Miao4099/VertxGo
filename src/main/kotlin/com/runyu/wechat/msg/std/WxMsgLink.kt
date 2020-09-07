package com.runyu.wechat.msg.std

import com.runyu.wechat.WxConst

/**
 * Created by sheng on 2017/10/24.
 */
class WxMsgLink(toUser:String, fromUser:String): WxMsg(toUser,fromUser,"link"){
    override fun getCDataFields():Set<String>{
        return super.getCDataFields()
                .plus(WxConst.FieldName.TITLE)
                .plus(WxConst.FieldName.DESCRIPTION)
                .plus(WxConst.FieldName.URL)
    }


    constructor(toUser:String, fromUser:String, title:String, description:String,url:String="") : this(toUser, fromUser) {
        mJson.put(WxConst.FieldName.TITLE,title)
        mJson.put(WxConst.FieldName.DESCRIPTION,description)
        mJson.put(WxConst.FieldName.URL,url)
    }
}