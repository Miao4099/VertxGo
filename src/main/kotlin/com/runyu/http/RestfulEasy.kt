package com.runyu.http

import com.runyu.std.Log
import com.runyu.std.Msg
import com.runyu.user.CusUser
import com.runyu.worker.WorkHttpAgent
import com.runyu.worker.getParamFromHeader
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.RoutingContext

class RestfulEasy(var root:String,var pathInfo:Map<String,PathInfo>, router: Router, agent: WorkHttpAgent,timeout:Long=HTTP_DEFAULT_TIMEOUT ):Restful(router,agent,timeout) {

    fun getPathInfo(path:String):PathInfo?{
        var p=path.removePrefix(root)
        return pathInfo[p]
    }

    override fun getHandler(cmd:String): (r: RoutingContext) -> Unit= { rc ->
        //截取路徑和動作作爲msgId
        var path=rc.normalisedPath()
        var pi=getPathInfo(path)

        if(null!=pi) {
            var req = Msg().msgId(pi.msgId)

            //加入post的所有参数
            rc.request().formAttributes().forEach {
                req.add(it.key, it.value)
            }

            //将body中json数据全体作为json参数
            Log.e("body", rc.body)
            rc.bodyAsJson?.let {
                req.param(it)
                Log.e("body", rc.bodyAsJson)
            }


            req=mergeExtra(pi.jsonExtra,req)

            sendMsg(rc, pi.workId, req, true, true)
        }else{
            Log.e("RestfulEasy","not handled for:",path)
        }
    }

    override fun addHandler(path:String,commands:Set<String>?,root:String?){
        router.post("$root$path").handler { rc ->
            getHandler(path).invoke(rc)
        }
    }

    init{
        pathInfo.forEach { path, u ->
            addHandler(path,null,root)
        }
    }
}