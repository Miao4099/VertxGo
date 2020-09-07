package com.runyu.worker


import com.runyu.com.runyu.worker.WorkerMaster
import com.runyu.http.GET
import com.runyu.http.POST
import com.runyu.http.replyJson
import com.runyu.std.*
import com.runyu.user.CusUser

import io.vertx.core.http.HttpMethod
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.http.HttpServerRequest
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.RoutingContext


abstract class WorkHttp():WorkShop(){

    public var mLoginStateReceiver="sql"
    protected var mMenu=JsonArray()

    override fun start(config: String): WorkShop {
        super.start(config)

        var cfg = AnyJson(config)
        mMenu=cfg.getJsonArray("menu", JsonArray())
        return this
    }

    protected fun replyJson(rc: RoutingContext,json:JsonObject){
        rc.request().response().replyJson(json)
    }


    fun setStd404(router:Router){
        router.route().handler({ctx ->
            Log.e("---->agent ${ctx.normalisedPath()} not handled")
            ctx.fail(404);
        });
    }


    fun setStdUserRouter(router:Router,root:String?=null){
        router.POST("/user/login",this::handleLoginIn)
        router.GET("/user/info",this::handleLoginInfo)
        router.POST("/user/logout",this::handleLoginOut)

        root?.let{
            router.POST("$root/user/login",this::handleLoginIn)
            router.GET("$root/user/info",this::handleLoginInfo)
            router.POST("$root/user/logout",this::handleLoginOut)
        }
    }







    protected open fun handleLoginIn(rc: RoutingContext){

        val userName = getParamFromBody<String>(rc, "username")
        val userPassword = getParamFromBody<String>(rc, "password")

        CusUser.loginUser(userName!!,userPassword!!,{ user, err->
            if(0==err.code) {
                var userInfo=user!!.toJson()

                replyJson(rc,Msg().good(userInfo))

                Log.e("user login",userInfo)

                //外发送登录成功消息
                VertMsg().post(mLoginStateReceiver,Msg().msgId("MSG_USER_LOGIN_SUCCESS").json(Msg().add("user_id",user!!.user_id)))
                //自动转发一个消息，用于记录每个app的请求参数和结果，忽略了html
                VertMsg().post("log_api", Msg().msgId(Const.MsgBody.MSG_LOG_API).add("req", Msg().add("user_name",userName).add("password",userPassword)).add("rsp", userInfo))
            }
            else
                replyJson(rc,Msg().bad(err.msg))
        }, {user,json->
            null
        })
    }


    protected fun handleLoginInfo(rc: RoutingContext){

        val token = getParamFromHeader<String>(rc,"token")

        var user= CusUser.getUser(token)
        if(null==user){
            replyJson(rc,Msg().error(Const.ErrUserNotFound()))
            return
        }

        var userInfo =user.toJson().add("menu",filterMenuByRole(mMenu,setOf(user.role)))
        replyJson(rc,Msg().good(userInfo))

    }


    protected fun handleLoginOut(rc: RoutingContext){

        val token = getParamFromHeader<String>(rc, "token")!!

        VertMsg().post("log_api", Msg().msgId(Const.MsgBody.MSG_LOG_API).add("req", Msg().add("token",token).add("rsp", JsonObject())))


        var user= CusUser.getUser(token)
        if(null==user){
            replyJson(rc,Msg().error(Const.ErrUserNotFound()))
            return
        }
        CusUser.logout(token)
        replyJson(rc,Msg().good("已经退出登录"))

    }


}

fun filterMenuByRole(menu:JsonArray,roles:Set<String>):JsonArray{
    return AnyJson.filterJsonArray(menu,"children") {
        var role=it.getJsonObject("meta")?.getJsonArray("roles")
        if(null==role){
            true
        }else {
            var role: Set<Any> = role.toSet()
            roles.intersect(role).size > 0
        }
    }
}

inline fun getParamByName(rc: RoutingContext, key: String): String {
    try {
        var request: HttpServerRequest =rc.request()
        if (HttpMethod.GET == request.method())
            return request.getParam(key)
        else
            return request.getFormAttribute(key)
    } catch (e: Exception) {
        return "";
    }
}

inline fun <T> getParamFromBody(rc: RoutingContext, key: String): T? {
    try {
        var request: HttpServerRequest =rc.request()
        return rc.bodyAsJson.getValue(key) as T
    } catch (e: Exception) {
        return null;
    }
}

inline fun <T> getParamFromHeader(rc: RoutingContext, key: String): T? {
    try {
        var request: HttpServerRequest =rc.request()
        return rc.request().getHeader(key) as T
    } catch (e: Exception) {
        return null;
    }
}