package com.runyu.std

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import java.io.StringWriter
import java.util.ArrayList
import com.fasterxml.jackson.databind.type.TypeFactory





open class AnyJson(json: JsonObject? = null) : JsonObject() {
    init {
        json?.let {
            this.mergeIn(json)
        }
    }

    constructor(text:String) : this() {
        this.mergeIn(JsonObject(text))
    }

    companion object {
        fun toString(v:Any):String{
            val mapper = ObjectMapper().findAndRegisterModules()
            var writer= StringWriter()
            mapper.writeValue(writer,v)
            return writer.buffer.toString()
        }



        fun toArray(v:Any):JsonArray{
            val mapper = ObjectMapper().findAndRegisterModules()
            var writer= StringWriter()
            mapper.writeValue(writer,v)
            return JsonArray(writer.buffer.toString())
        }

        fun toJson(v:Any): AnyJson {
            val mapper = ObjectMapper().findAndRegisterModules()
            var writer= StringWriter()
            mapper.writeValue(writer,v)
            return AnyJson(writer.buffer.toString())
        }

        fun loopJsonArrayAsArray(array:JsonArray,block:(item:JsonArray)->Unit){
            array.forEach {
                block.invoke(it as JsonArray)
            }
        }

        fun loopJsonArrayAsObject(array:JsonArray,block:(item:JsonObject)->Unit){
            array.forEach {
                block.invoke(it as JsonObject)
            }
        }

        fun filterJsonArray(array:JsonArray,childKeyName:String,block:(item:JsonObject)->Boolean):JsonArray{
            var out=JsonArray()
            array.forEach { it as JsonObject

                var childs=it.getJsonArray(childKeyName,null)

                if(null!=childs) {
                    if(block.invoke(it)) {
                        var one = it
                        one.put(childKeyName, filterJsonArray(childs, childKeyName, block))
                        out.add(one)
                    }
                }
                else
                    if(block.invoke(it))out.add(it)
            }
            AssertionError(out.size()>0)
            return out
        }

        inline fun toList(jsonString:String,clazz:Class<*>):ArrayList<*>{
            val mapper = ObjectMapper().findAndRegisterModules()
            return mapper.readValue(jsonString,
                    mapper.typeFactory.constructParametricType(ArrayList::class.java, clazz))
        }
    }

//    override fun toString():String{
//        val mapper = ObjectMapper().findAndRegisterModules()
//        var writer= StringWriter()
//        mapper.writeValue(writer,this)
//        return writer.buffer.toString()
//    }


    open fun error(code:Int, msg:String=""):AnyJson{
        this.put("code",code).put("message",msg)
        return this
    }

    fun get():String{
        return this.toString()
    }

    fun errCode():Int{
        return this.getInteger("code")
    }

    fun errMsg():String{
        return this.getString("message")
    }

    fun <T> getSingle(key:String,subkey:String):T{
        return getJsonArray(key).getJsonObject(0).getValue(subkey) as T
    }

    fun <T> getFirstItem(key:String,pos:Int=0):T{
        return getJsonArray(key).getJsonArray(0).getValue(pos) as T
    }


    /**
     * 将一个JsonArray按指定key循环输出对应值
     */
    fun getArray(key:String,fieldKey:String,action:(key:String,value:Any)->Unit){
        try {
            var arr: JsonArray = getJsonArray(key)
            for (item in arr) {
                val j = AnyJson(item.toString())
                action(j.get(fieldKey), item)
            }
        }catch (e:Exception){
            e.printStackTrace()
            Log.e("getArray", e.message.toString())
        }
    }

    fun <T> get(key:String,default:T):T{
        if(super.containsKey(key))
            return super.getValue(key) as T
        else
            return default
    }

    fun <T> get(key:String):T{
        return super.getValue(key) as T
    }

    open fun add(key:String,value:Any?): AnyJson {
        put(key,value);
        return this;
    }

    fun add(value:JsonObject): AnyJson {
        mergeIn(value)
        return this;
    }

    fun del(key:String):AnyJson{
        remove(key)
        return this
    }
}

fun JsonObject.filterFields(filter:Set<String>):JsonObject{
    filter.forEach({this.remove(it)})
    return this
}

fun JsonObject.nullFields(filter:Set<String>):JsonObject{
    filter.forEach({this.putNull(it)})
    return this
}