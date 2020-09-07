package com.runyu.com.runyu.http

import com.runyu.http.HTTP_DEFAULT_TIMEOUT
import com.runyu.http.Restful
import com.runyu.http.RestfulPost
import com.runyu.std.Log
import com.runyu.std.Msg
import com.runyu.user.CusUser
import com.runyu.user.User
import com.runyu.worker.WorkHttpAgent
import com.runyu.worker.getParamFromHeader
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.RoutingContext

class RestfulAny(root:String, path:String, router: Router, agent: WorkHttpAgent, dbWorkId:String, roles:Set<String> =setOf(), jsonExtra: JsonObject?=null, timeout:Long=HTTP_DEFAULT_TIMEOUT)
    : RestfulPost(root,path,router,agent,dbWorkId,roles,setOf("*"),jsonExtra,timeout){

    override fun getMsgId(rc: RoutingContext,path:String,cmd:String):String{
        var p = path.substringAfterLast("/")
        var realCmd=rc.normalisedPath().substringAfterLast("/")
        return "MSG_${p.toUpperCase()}_${realCmd.toUpperCase()}"
    }


}