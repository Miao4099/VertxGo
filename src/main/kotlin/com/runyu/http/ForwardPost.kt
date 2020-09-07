package com.runyu.http

import com.runyu.std.Log
import com.runyu.std.Msg
import com.runyu.user.CusUser
import com.runyu.user.User
import com.runyu.worker.WorkHttpAgent
import com.runyu.worker.getParamFromHeader
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.RoutingContext

class ForwardPost(root:String,path:String, router: Router, agent: WorkHttpAgent, dbWorkId:String, roles:Set<String> =setOf(), var validCmds:Set<String>, jsonExtra: JsonObject?=null,timeout:Long=HTTP_DEFAULT_TIMEOUT)
    :RestfulPost(root,path,router,agent,dbWorkId,roles,validCmds,jsonExtra,timeout){

    override fun getMsgId(rc: RoutingContext,path:String,cmd:String):String{
        var p = path.substringAfterLast("/")
        return "MSG_${p.toUpperCase()}_${cmd.toUpperCase()}"
    }

    override fun judgeIfHasRight(user: User, cmd:String):Boolean{
        return roles.contains(user.role) && validCmds.contains(cmd)
    }

}