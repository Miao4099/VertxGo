package com.runyu.worker

import com.runyu.sql.*
import com.runyu.std.*
import com.runyu.std.Const.MsgBody.MSG_LOG_API
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.eventbus.Message
import io.vertx.reactivex.ext.jdbc.JDBCClient


open class WorkSupport: WorkSql() {

    override fun start(config: String): WorkShop {
        super.start(config)

        mClient = JDBCClient.create(Vertx(),AnyJson(config))

        Dispatcher()
                .add(MSG_LOG_API, this::msgLogApi)
        return this;
    }


    fun msgLogApi(key: String, msg: JsonObject, message: Message<JsonObject>): Msg {
        return tryDo(message) {
  //          var id= IDGen.strId()
            var sql = SqlMaker("logs")
                    .set("created_time", now())
                    .set("updated_time", now())
                    .set("msg_id", msg.getJsonObject("req").getString("msg_id"))
                    .set("req", msg.getJsonObject("req").toString())
                    .set("rsp", msg.getJsonObject("rsp").toString())
                    .getInsert()

            singleAction(sql, message)
        }
    }

}



