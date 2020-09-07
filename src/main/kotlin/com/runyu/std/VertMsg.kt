package com.runyu.std

import com.runyu.com.runyu.worker.WorkerMaster
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.core.eventbus.Message

class VertMsg{

    fun post(rxId:String,msg:JsonObject,block:((rsp:JsonObject)->Unit)?=null,fail:((rsp:JsonObject)->Unit)?=null){
        WorkerMaster.Vertx().eventBus().request(rxId, msg, Handler<AsyncResult<Message<JsonObject>>>() {
            if (it.succeeded()) {
                var ret=it.result().body().toString()
                Log.e("VertMsg","Received rsp: " + ret)
                block?.invoke(JsonObject(ret))
            }else{
                Log.e("VertMsg","Bad rsp: " )
                fail?.invoke(JsonObject())
            }
        })
    }

}