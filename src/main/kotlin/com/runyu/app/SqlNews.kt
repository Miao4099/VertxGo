package com.runyu.app

import com.runyu.com.runyu.app.`val`.*
import com.runyu.sql.SqlBuilder
import com.runyu.sql.*
import com.runyu.std.*
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.eventbus.Message


var sqlNews= SqlBuilder("news",mapOf(
        Pair("id","news_id"),
        Pair("image","news_image"),
        Pair("brief","news_brief"),
        Pair("date","news_date"),
        Pair("state","news_state"),
        Pair("content","news_content"),
        Pair("video","news_video"),
        Pair("url","news_url"),
        Pair("created_at","created_time")

)
)


fun AppMySql.msgNewsAdd(key: String, msg: JsonObject, message: Message<JsonObject>): Msg {
    return tryDo(message) {
        var id= IDGen.strId()
        var sql = sqlNews.sql()
                .set("news_id", id)
                .set("created_time", now())
                .set("updated_time", now())
                .set("news_state", 2)
                .set<String>("news_date",msg, ValDate())
                .set<String>("news_brief",msg, ValBrief())
                .set<String>("news_content",msg, ValBase64())
                .getInsert()

        transaction(sql.toArray(), message,{msg->
            msg.add("news_id",id)

        })
    }
}


fun AppMySql.msgNewsUpdate(key: String, msg: JsonObject, message: Message<JsonObject>):Msg {
    return tryDo(message) {
        var sql = sqlNews.sql()
                .set("news_date",msg.jsonGet<String>("date"),
                    ValDate()
                )
                .set<Int>("news_state",msg, ValNewsState())
                .set<String>("news_brief",msg, ValBrief())
               // .set<String>("news_image",msg,ValFileName())
                .set<String>("news_content",msg, ValBase64())
                .set<String>("news_date",msg, ValDate())
                .getUpdate("where news_id='${msg.jsonGet<String>("id")}'")

        transaction(sql.toArray(), message)
    }
}




fun AppMySql.msgNewsDel(key: String, msg: JsonObject, message: Message<JsonObject>):Msg{
    return tryDo(message) {
        //删除时都是多个id一起上传
        var sql=sqlNews.sql()
                .getDelete("news_id",msg.jsonGet<JsonArray>("ids")!!)

        transaction(sql, message)
    }
}



fun AppMySql.msgNewsGet(key: String, msg: JsonObject, message: Message<JsonObject>):Msg{
    return tryDo(message) {
        //可以写在下面set位置，也可以写在这里
        ValId().validate(msg.jsonGet<String>("id"))

        var sql = sqlNews.sql()
                .set("news_id")
                .set("news_date")
                .set("news_brief")
                .set("news_image")
                .set("news_state")
                .set("updated_time")
                .set("news_content")
                .set("news_video")
                .getList("where news_id='${msg.jsonGet<String>("id")}'")

        getQuery(sql, message,{json->
            addNewsField(json)
        })
    }
}


fun AppMySql.msgNewsList(key: String, msg: JsonObject, message: Message<JsonObject>): Msg {
    return tryDo(message) {
        var pi=msg.pageInfo()

        var sql = sqlNews.sql()
                .set("news_id")
                .set("news_date")
                .set("news_brief")
                .set("news_image")
                .set("news_state")
                .set("updated_time")
                .set("news_content")
                .set("news_video")
                .getList(sqlNews.fs(msg), sqlNews.qs(msg), pi)
        listQuery(sql, message,{json->
            var qr=QueryResult(json)
            qr.loopRows { addNewsField(it) }
        })
    }
}


fun AppMySql.msgNewsVideo(key: String, msg: JsonObject, message: Message<JsonObject>):Msg {
    return tryDo(message) {
        var sql = sqlNews.sql()
                .set("news_video",msg.jsonGet<String>("filename"),
                    ValFileName()
                )
                .getUpdate("where news_id='${msg.jsonGet<String>("id")}'")

        transaction(sql.toArray(), message)
    }
}


fun AppMySql.msgNewsImage(key: String, msg: JsonObject, message: Message<JsonObject>):Msg {
    return tryDo(message) {
        var sql = sqlNews.sql()
                .set("news_image",msg.jsonGet<String>("filename"),
                    ValFileName()
                )
                .getUpdate("where news_id='${msg.jsonGet<String>("id")}'")

        transaction(sql.toArray(), message)
    }
}

fun AppMySql.addNewsField(row:JsonObject):JsonObject{
    row.getString("video")?.let {
        row.put("video", mOssClient?.signatureUrl(it))
    }
    row.getString("image")?.let {
        row.put("image", mOssClient?.signatureUrl(it))
    }
    return row
}