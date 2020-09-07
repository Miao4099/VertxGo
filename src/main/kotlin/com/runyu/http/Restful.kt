package com.runyu.http

import com.runyu.com.runyu.worker.WorkerMaster
import com.runyu.std.Const
import com.runyu.std.Msg
import com.runyu.std.VertMsg
import com.runyu.worker.WorkHttpAgent
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.eventbus.Message
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.RoutingContext

var HTTP_DEFAULT_TIMEOUT =15000L

open abstract class Restful(var router: Router, var agent: WorkHttpAgent,var timeout:Long=HTTP_DEFAULT_TIMEOUT) {
    data class PathInfo(
            var workId:String,
            var msgId:String,
            var jsonExtra:JsonObject?=null
    )


    open fun sendMsg(rc: RoutingContext,workId:String, msg: Msg, hasUser:Boolean, hasRight:Boolean){
        if(hasUser&&hasRight) {
            agent.send2Other(workId, msg, Handler<AsyncResult<Message<JsonObject>>> {
                if (it.succeeded()) {
                    val rsp = Msg(it.result().body())
                    //如果是html返回就replyHtml，否则replyJson
                    if(rsp.containsKey("html")){
                        rc.request().response().replyHtml(rsp.html())
                    }else {
                        var errCode = rsp.errCode()
                        if (0 == errCode) {
                            rc.request().response().replyJson(Msg().good(rsp.delError()))//去除原始err_code
                        } else {
                            rc.request().response().replyJson(Msg().bad(errCode, rsp.errMsg()))
                        }
                        //自动转发一个消息，用于记录每个app的请求参数和结果，忽略了html
                        VertMsg().post("log_api", Msg().msgId(Const.MsgBody.MSG_LOG_API).add("req", msg).add("rsp", rsp))
                    }
                } else {
                    rc.request().response().replyJson(Msg().error(Const.BadResponse()))
                }

            }, timeout)
        }else if(!hasUser)
            rc.request().response().replyJson(Msg().error(Const.ErrUserNotLogin()))
        else if(!hasRight)
            rc.request().response().replyJson(Msg().error(Const.ErrUserNoRight()))
        else
            rc.request().response().replyJson(Msg().error(Const.ErrCantBeHere()))
    }


    open fun addHandler(path:String,commands:Set<String>?=null,root:String?=null){
        commands?.forEach {cmd->
            router.post("$path/$cmd").handler { rc ->
                //setExpectMultipart(true)必须在，否则post的参数收不到
                //但是HttpAgent初始化话时，添加了默认的bodyHandler，这样才可以收到body，但是与endHandler冲突
//                rc.request().setExpectMultipart(true)
//                rc.request().endHandler {
                getHandler(cmd).invoke(rc)
                //        }
            }
            root?.let {
                router.post("$root$path/$cmd").handler { rc ->
                    getHandler(cmd).invoke(rc)
                }
            }
        }
    }

    protected open fun getMsgId(r: RoutingContext,path:String,cmd:String):String{
        var p = path.substringAfterLast("/")
        return "MSG_${p.toUpperCase()}_${cmd.toUpperCase()}"
    }

   abstract fun getHandler(cmd:String): (r: RoutingContext) -> Unit


    protected fun mergeExtra(extra:JsonObject?,req:Msg):Msg{
        extra?.let{//extra不为空再操作
            req.getString("json")?.let{json-> //取出json全部
                var hasOneAlready=null!=Msg(json).getJsonArray("filters")
                if(hasOneAlready) {
                    var j = Msg(json).getJsonArray("filters")?.let {
                        it.addAll(extra.getJsonArray("filters"))
                    }
                    req.put("json", Msg(json).put("filters", j))
                }else{//否则替换为extra
                    req.put("json", Msg(json).put("filters", extra.getJsonArray("filters")))
                }
            }
        }
        return req
    }

}