package com.runyu.wechat.msg.std


import com.runyu.wechat.WxConst
import com.runyu.wechat.msg.cus.CusMsgArticle
import io.vertx.core.json.JsonObject

import java.util.*

/**
 * Created by sheng on 2017/10/24.
 */
class WxMsgArticle(toUser:String, fromUser:String): WxMsg(toUser, fromUser,"news"){

    var mItems=ArrayList<Item>()

    override fun getCDataFields():Set<String>{
        return super.getCDataFields()
                .plus(WxConst.FieldName.TITLE)
                .plus(WxConst.FieldName.DESCRIPTION)
                .plus(WxConst.FieldName.URL)
                .plus(WxConst.FieldName.PIC_URL)
    }



    fun addArticle(title:String, description:String,url:String="",picUrl:String=""):WxMsgArticle{
        mItems.add(Item(title, description, url, picUrl))
        return this
    }

    override fun toXml():String{
        var items:String=""
        for(item in mItems){
            items+=makeXml(item.toJson(),"item")
        }
        mJson.put(WxConst.FieldName.ARTICLE_COUNT,mItems.size)
        var xml=makeXml(mJson)
        xml=xml.replaceFirst("</ArticleCount>","</ArticleCount><Articles>")
        xml=xml.replaceFirst("</xml>",items+"</Articles></xml>")
        return xml
    }

    data class Item(var Title:String,var Description:String,var Url:String="",var PicUrl:String=""){
        fun toJson():JsonObject{
            var item=JsonObject()
            item.put(WxConst.FieldName.TITLE,Title)
            item.put(WxConst.FieldName.DESCRIPTION,Description)
            item.put(WxConst.FieldName.PIC_URL,PicUrl)

            Url=Base64.getEncoder().encodeToString(Url.toByteArray())
            item.put(WxConst.FieldName.URL,Url)
            return item
        }

    }


    override fun toCusMsg(): CusMsgArticle?{
        var msg= CusMsgArticle(toUser())
        mItems.forEach {
            item->
              msg.addArticle(item.Title,item.Description,item.Url,item.PicUrl)
        }
        return msg
    }
    companion object {
        fun fromWxMsg(wxMsg: WxMsg): WxMsgArticle? {
            if ("news" == wxMsg.msgType()) {
                var msg = WxMsgArticle(wxMsg.toUser(), wxMsg.fromUser())
                var json=WxMsg(wxMsg.toXml())


                return msg
            }
            return null
        }
    }
}

