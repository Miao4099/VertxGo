package com.runyu.app

import com.runyu.com.runyu.http.RestfulAny
import com.runyu.com.runyu.worker.WorkerMaster
import com.runyu.http.Restful.PathInfo
import com.runyu.http.RestfulEasy
import com.runyu.http.*
import com.runyu.sql.QueryFilter
import com.runyu.sql.QuerySorter
import com.runyu.std.Const
import com.runyu.std.Log
import com.runyu.std.Msg
import com.runyu.std.VertMsg
import com.runyu.user.CusUser
import com.runyu.worker.WorkHttpAgent
import com.runyu.worker.WorkShop
import com.runyu.worker.getParamFromBody
import io.vertx.reactivex.core.http.HttpServer
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.RoutingContext


class AppHttpAgent: WorkHttpAgent() {
    val API="api"

    var mAppKey=""
    var mSecret=""
    override fun start(config: String): WorkShop {
        mAppKey=Msg(config).getJsonObject("wx_app").getString("app_key",null)
        mSecret=Msg(config).getJsonObject("wx_app").getString("secret",null)
        super.start(config)
        return this
    }


    override fun addHandler(router: Router) {

        setBrowserHistoryPath(setOf("/shengChan","/cangChu","/baoGao"),"/index.html")

        setStdStaticHtml("/admin","admin")
       FILE("/favicon.ico","admin/favicon.ico")

        setStdUserRouter(mRouter!!,"/admin")

        var adminAccess=setOf("admin")
        var editorAccess=setOf("admin","editor")
        var vipAccess=setOf("vip")
        var allAccess=setOf("admin","editor","vip")

        RestfulAny("/admin","/user",router,this,"sql",adminAccess)
        RestfulPost("/admin","/vip",router,this,"sql",editorAccess,setOf("login","logout","addVip","updateVip","password"))

        RestfulPost("/admin","/news",router,this,"sql",editorAccess,setOf("video","background"))

        // 无权限校验路径，小程序用，/api开头
        var qf=QueryFilter().add("state","=",1).get()
        var extra=Msg().add("filters",qf)


        WxHandler(mAppKey,mSecret,"/app",router,this,"sql")
                .setHandler { user,userInfo->
                    Log.e("user create",userInfo)
                    VertMsg().post("sql",Msg().msgId("MSG_USER_CREATE").addUserFields(user).json(userInfo))
                }


        setStd404(mRouter!!)


    }



    override fun setupWebsocketHandler(server: HttpServer, router: Router) {

    }


    protected fun handleVipLoginIn(rc: RoutingContext){

        val userName = getParamFromBody<String>(rc, "username")
        val userPassword = getParamFromBody<String>(rc, "password")

        CusUser.loginUser(userName!!,userPassword!!,{ user, err->
            if(0==err.code) {

                if("vip"==user!!.role) {
                    var userInfo = user!!.toJson()

                    replyJson(rc, Msg().good(userInfo))

                    //外发送登录成功消息
                    VertMsg().post(mLoginStateReceiver, Msg().msgId("MSG_USER_LOGIN_SUCCESS").json(Msg().add("user_id", user!!.user_id)))

                    //自动转发一个消息，用于记录每个app的请求参数和结果，忽略了html
                    VertMsg().post("log_api", Msg().msgId(Const.MsgBody.MSG_LOG_API).add("req", Msg().msgId("MSG_USER_LOGIN").add("user_name",userName).add("password",userPassword)).add("rsp", userInfo))

                }else{
                    replyJson(rc,Msg().bad("不是VIP客户"))
                }
            }
            else
                replyJson(rc,Msg().bad(err.msg))
        }, {user,json->
            null
        })
    }

    fun setVipRouterLogin(router:Router, root:String){
        router.POST("/vip/login",this::handleVipLoginIn)
        root?.let {
            router.POST("$root/vip/login", this::handleVipLoginIn)
        }
    }

    //这里覆盖了默认的登陆
    override fun handleLoginIn(rc: RoutingContext){

        val userName = getParamFromBody<String>(rc, "username")
        val userPassword = getParamFromBody<String>(rc, "password")

        CusUser.loginUser(userName!!,userPassword!!,{ user, err->
            if(0==err.code) {
                //发布上线是限制访问路径
             //   if(setOf("admin","editor").contains(user!!.role)&&rc.normalisedPath().contains("admin")) {
               if(setOf("admin","editor").contains(user!!.role)) {
                    var userInfo = user!!.toJson()

                    replyJson(rc, Msg().good(userInfo))

                    //外发送登录成功消息
                    VertMsg().post(mLoginStateReceiver, Msg().msgId("MSG_USER_LOGIN_SUCCESS").json(Msg().add("user_id", user!!.user_id)))

                   //自动转发一个消息，用于记录每个app的请求参数和结果，忽略了html
                   VertMsg().post("log_api", Msg().msgId(Const.MsgBody.MSG_LOG_API).add("req", Msg().msgId("MSG_USER_LOGIN").add("user_name",userName).add("password",userPassword)).add("rsp", userInfo))

               }else{
                    replyJson(rc,Msg().error(106,Const.Prompt.USER_NO_RIGHT))
                }
            }
            else
                replyJson(rc,Msg().bad(err.msg))
        }, {user,json->
            null
        })
    }

}
