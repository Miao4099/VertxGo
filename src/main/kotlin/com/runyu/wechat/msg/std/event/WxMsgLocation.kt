package com.runyu.wechat.msg.std.event

import com.runyu.wechat.WxConst


/**
 * Created by sheng on 2017/10/24.
 */
class WxMsgLocation(toUser:String, fromUser:String): WxMsgEvent(toUser,fromUser,"LOCATION"){
    constructor(toUser:String, fromUser:String, lat:Float,lon:Float,precision:Float) : this(toUser,fromUser) {
        mJson.put(WxConst.FieldName.LATITUDE,lat)
        mJson.put(WxConst.FieldName.LONGITUDE,lon)
        mJson.put(WxConst.FieldName.PRECISION,precision)
    }
}