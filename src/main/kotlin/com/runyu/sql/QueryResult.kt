package com.runyu.sql

import com.fasterxml.jackson.databind.ObjectMapper
import com.runyu.std.AnyJson
import com.runyu.std.Jos.analysisClassInfo
import com.runyu.std.get
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import java.util.*


class QueryResult(var json:JsonObject){
    fun numOfCol():Int{
        return AnyJson(json).get("numColumns",0)
    }

    fun numOfRow():Int{
        return AnyJson(json).get("numRows",0)
    }

    /**
     * 返回反析構后的數據
     */
    inline fun <reified T> rows(): ArrayList<T> {
        val mapper = ObjectMapper().findAndRegisterModules()
        return mapper.readValue(json.getJsonArray("rows").toString(),
                mapper.typeFactory.constructParametricType(ArrayList::class.java, T::class.java))
    }

    /**
     * 返回josn格式的數據
     */
    fun rows():JsonArray{
        return json.getJsonArray("rows")
    }

    inline fun <reified T> one():T?{
        if(1==numOfRow()){
            return rows<T>()[0]
        }
        return null
    }

    fun first():JsonObject?{
        if(numOfRow()>0){
            return json.getJsonArray("rows").getJsonObject(0)
        }
        return null
    }

    fun hypertext():String{
        return String(Base64.getDecoder().decode(first()!!.getString("hyper_content")))
    }

    fun clean():JsonObject{
        var dropKeys=setOf("sql","columnNames","numColumns","results")
        dropKeys.forEach { json.remove(it) }

        dropKeys=setOf("pAGESIZE","pAGEINDEX","cOUNT")
        rows().forEach { row->row as JsonObject
            dropKeys.forEach { row.remove(it) }
        }
        return json
    }


    fun loopRows(block:(JsonObject)->Unit):JsonObject{
        rows().forEach {
            block.invoke(it as JsonObject)
        }
        return json
    }

    inline fun <reified T> loopItems(block:(T)->Unit):JsonObject{
        rows<T>().forEach {
            block.invoke(it as T)
        }
        return json
    }

    fun sum (fieldName:String):Int{
        var total=0
        loopRows { total += (it.getInteger(fieldName)!!)}
        return total
    }
}