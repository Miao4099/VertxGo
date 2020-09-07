package com.runyu.com.runyu.wechat.msg.app

import com.runyu.std.AnyJson
import com.runyu.std.Msg
import com.runyu.std.get
import io.vertx.core.json.JsonObject

class WxSubscribeMsg(var touser:String,
                     var template_id:String,
                     var data:Map<String,Any>,
                     var page:String="index",
                     var miniprogram_state:String="developer",
                     var lang:String="zh_CN") {

    companion object{
        fun parse(json:JsonObject):WxSubscribeMsg{
            var d=mapOf<String,Any>()

            var dd=json.getJsonObject("data")
            json.getJsonObject("data").fieldNames().forEach {
                d=d.plus(Pair(it,dd.getJsonObject(it).get("value")!!))
            }

            return WxSubscribeMsg(json.getString("touser"),
            json.getString("template_id"),
            d,
            json.getString("page","index"),
            json.getString("miniprogram_state","developer"),
            json.getString("lang","zh_CN")
            )
        }
    }

     fun build():JsonObject{
         var d= Msg()
         data.forEach{d.add(it.key,Msg().add("value",it.value))}
         return Msg().add("touser",touser)
             .add("template_id",template_id)
             .add("page",page)
             .add("miniprogram_state",miniprogram_state)
             .add("lang",lang)
             .add("data",d)
     }

}