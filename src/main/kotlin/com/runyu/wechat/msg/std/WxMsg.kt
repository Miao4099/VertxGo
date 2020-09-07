package com.runyu.wechat.msg.std


import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.runyu.std.AnyJson
import com.runyu.wechat.WxConst
import com.runyu.wechat.msg.cus.CusMsg
import io.vertx.core.json.JsonObject
import java.util.*

/**
 * Created by sheng on 2017/10/24.
 */
var mXmlMapper: XmlMapper?=null
var mJsonMapper: ObjectMapper?=null

fun Xml():XmlMapper{
    if(null== mXmlMapper)
        mXmlMapper =XmlMapper()
    return mXmlMapper as XmlMapper
}

fun Json():ObjectMapper{
    if(null== mJsonMapper)
        mJsonMapper =ObjectMapper()
    return mJsonMapper as ObjectMapper
}



open class WxMsg: AnyJson {
    protected lateinit var mJson:JsonObject
    constructor(toUser:String,fromUser:String,type:String){
        mJson.put(WxConst.FieldName.TO_USERNAME,toUser)
        mJson.put(WxConst.FieldName.FROM_USERNAME,fromUser)
        mJson.put(WxConst.FieldName.MSG_TYPE,type)
        mJson.put(WxConst.FieldName.CREATE_TIME,Date().time)
    }




    constructor(xml:String){
        //这里实际上只处理的xml的第一层，但是没关系
        var mXmlMapper = Xml()
        var entries = mXmlMapper.readValue(xml, HashMap::class.java)

        var jsonMapper = Json()
        mJson= JsonObject(jsonMapper.writeValueAsString(entries))

    }

    constructor(json:JsonObject){
        mJson=json
    }

    open fun toXml():String{
        return makeXml(mJson)
    }

    open fun makeXml(o:JsonObject,name:String="xml"):String{
        var xml= Xml().writeValueAsString(o)
        xml=xml.replaceFirst("</map><empty>false</empty></JsonObject>","</$name>")
        xml=xml.replaceFirst("<JsonObject xmlns=\"\"><map>","<$name>")

        //将字符串类型加入CDATA
        for(f in getCDataFields()){
            var ov=o.getString(f)
            var v:String?=ov
            if(f in getBase64Fields()&&null!=ov){
                v=String(Base64.getDecoder().decode(ov))
            }
            var value="<"+f+">"+"<![CDATA["+v+"]]>"
            xml=xml.replaceFirst("<"+f+">"+ov,value)
        }

        return xml
    }




    open fun toCusMsg(): CusMsg?{
        return null
    }



    open fun getCDataFields():Set<String>{
        return setOf(WxConst.FieldName.TO_USERNAME,
                WxConst.FieldName.FROM_USERNAME,
                WxConst.FieldName.MSG_TYPE
        )
    }

    open fun getBase64Fields():Set<String>{
        return setOf(WxConst.FieldName.URL)

    }

    fun msgId():String{
        return mJson.getString(WxConst.FieldName.MSG_ID,"")
    }

    fun toUser():String{
        return mJson.getString(WxConst.FieldName.TO_USERNAME,"")
    }

    fun fromUser():String{
        return mJson.getString(WxConst.FieldName.FROM_USERNAME,"")
    }

    fun msgType():String{
        return mJson.getString(WxConst.FieldName.MSG_TYPE,"")
    }

    fun toUser(v:String){
        mJson.put(WxConst.FieldName.TO_USERNAME,v)
    }

    fun fromUser(v:String){
        mJson.put(WxConst.FieldName.FROM_USERNAME,v)
    }

    fun msgType(v:String){
        mJson.put(WxConst.FieldName.MSG_TYPE,v)
    }



    fun createTime(): Date {
        return Date(mJson.getLong(WxConst.FieldName.CREATE_TIME))
    }
    ///////////////////////////////////////////////////////////////
    //Text Message
    fun content():String{
        return mJson.getString(WxConst.FieldName.CONTENT,"")
    }

    fun content(v:String){
        mJson.put(WxConst.FieldName.CONTENT,v)
    }

    ///////////////////////////////////////////////////////////////
    //Event Message
    fun event():String{
        return mJson.getString(WxConst.FieldName.EVENT,"")
    }

    fun event(v:String){
        mJson.put(WxConst.FieldName.EVENT,v)
    }

    ///////////////////////////////////////////////////////////////
    //Voice Message
    fun mediaId():String{
        return mJson.getString(WxConst.FieldName.MEDIA_ID,"")
    }

    fun mediaId(v:String){
        mJson.put(WxConst.FieldName.MEDIA_ID,v)
    }

    fun format():String{
        return mJson.getString(WxConst.FieldName.FORMAT,"")
    }

    fun format(v:String){
        mJson.put(WxConst.FieldName.FORMAT,v)
    }

    fun recognition():String{
        return mJson.getString(WxConst.FieldName.RECOGNITION,"")
    }


    fun picUrl():String{
        return mJson.getString(WxConst.FieldName.PIC_URL,"")
    }

    fun picUrl(v:String){
        mJson.put(WxConst.FieldName.PIC_URL,v)
    }


    fun title():String{
        return mJson.getString(WxConst.FieldName.TITLE,"")
    }

    fun title(v:String){
        mJson.put(WxConst.FieldName.TITLE,v)
    }


    fun description():String{
        return mJson.getString(WxConst.FieldName.DESCRIPTION,"")
    }

    fun description(v:String){
        mJson.put(WxConst.FieldName.DESCRIPTION,v)
    }


    fun url():String{
        return mJson.getString(WxConst.FieldName.URL,"")
    }

    fun url(v:String){
        mJson.put(WxConst.FieldName.URL,v)
    }


}