package com.runyu.wechat.msg.cus


import com.runyu.wechat.WxConst
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import java.util.ArrayList

/**
 * Created by sheng on 2017/10/24.
 */
class CusMsgArticle(toUser:String): CusMsg(toUser, "news"){

    var mItems= ArrayList<CusMsgArticle.Item>()

    fun addArticle(title:String, description:String,url:String="",picUrl:String=""){
        mItems.add(CusMsgArticle.Item(title, description, url, picUrl))
    }

    fun build():String{
        var items=JsonArray()
        for(item in mItems){
            items.add(item.toJson())
        }
        var j=JsonObject()
        j.put("articles",items)
        mJson.put("news",j)

        return mJson.toString()
    }

    data class Item(var Title:String,var Description:String,var Url:String="",var PicUrl:String=""){
        fun toJson(): JsonObject {
            var item= JsonObject()
            item.put(WxConst.FieldName.TITLE.toLowerCase(),Title)
            item.put(WxConst.FieldName.DESCRIPTION.toLowerCase(),Description)
            item.put(WxConst.FieldName.PIC_URL.toLowerCase(),PicUrl)
            item.put(WxConst.FieldName.URL.toLowerCase(),Url)
            return item
        }

    }

}