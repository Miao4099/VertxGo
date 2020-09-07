package com.runyu.wechat.msg.std.event

/**
 * Created by sheng on 2017/10/24.
 */
open class WxMsgUnsubscribe(toUser:String, fromUser:String): WxMsgEvent(toUser,fromUser,"unsubscribe")