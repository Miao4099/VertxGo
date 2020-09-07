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

/**
 * path:要配置的路由的根路径
 * router:路由器
 * agent:http agent
 * dbWorkId：要转发消息的接收work id
 * roles:用户需要的权限，与user表中的role对应
 * cmds:需要在5个cmd外增加的cmd
 * timeout：这个restful的超时设置
 */
open class RestfulPost(var root:String?=null, var path:String, router: Router, agent: WorkHttpAgent, var dbWorkId:String, var roles:Set<String> =setOf(), var cmds:Set<String>?=null,var jsonExtra:JsonObject?=null, timeout:Long=HTTP_DEFAULT_TIMEOUT)
    :Restful(router,agent,timeout){
    override fun getHandler(cmd:String): (r: RoutingContext) -> Unit= { rc ->
        //截取路徑和動作作爲msgId
     //   var p = path.substringAfterLast("/")
    //    var req = Msg().msgId("MSG_${p.toUpperCase()}_${cmd.toUpperCase()}")
        //   .add("group_id", user.groupId())
        var req=Msg().msgId(getMsgId(rc,path,cmd))

        var hasUser=false
        var hasRight=false
        val token = getParamFromHeader<String>(rc,"token")
        //檢查此用戶是否已經登錄，如果已經登錄則將token轉爲user信息加入消息
        CusUser.getUser(token)?.let{ user->
            //加入用户信息
            req.addUserFields(user)

            //加入post的所有参数
            rc.request().formAttributes().forEach {
                req.add(it.key, it.value)
            }

            //将body中json数据全体作为json参数
            Log.e("body",rc.body)
            rc.bodyAsJson?.let{
                req.param(it)
                Log.e("body",rc.bodyAsJson)
            }

            req=mergeExtra(jsonExtra,req)

            hasUser=true

            hasRight=judgeIfHasRight(user,cmd)
        }

        sendMsg(rc,dbWorkId,req,hasUser,hasRight)
    }


    open fun judgeIfHasRight(user: User,cmd:String):Boolean{
        return roles.contains(user.role)||roles.size==0//空集代表没有权限检查
    }


    init{
        var basicCmds=setOf("get","list","add","del","set","update")
        addHandler(path,basicCmds,root)
        cmds?.let {
            addHandler(path,it,root)
        }
    }
}