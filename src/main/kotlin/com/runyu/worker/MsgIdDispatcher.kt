package com.runyu.worker


import com.runyu.std.Const.MsgBody.MSG_ID
import io.vertx.core.json.JsonObject


/**
 * Created by sheng on 2017/6/1.
 */
class MsgIdDispatcher(): Dispatcher(){

    override fun key(msg: JsonObject):String?{
        return msg.getString(MSG_ID)
    }

}