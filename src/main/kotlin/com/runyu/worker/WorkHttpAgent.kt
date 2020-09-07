package com.runyu.worker

import com.runyu.http.replyJson
import com.runyu.std.AnyJson
import com.runyu.std.Const
import com.runyu.std.Log
import com.runyu.std.Msg
import com.runyu.user.CusUser

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.json.JsonObject
import io.vertx.core.net.JksOptions
import io.vertx.reactivex.core.eventbus.Message
import io.vertx.reactivex.core.http.HttpServer
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.RoutingContext
import io.vertx.reactivex.ext.web.handler.BodyHandler
import io.vertx.reactivex.ext.web.handler.CorsHandler
import io.vertx.reactivex.ext.web.handler.StaticHandler


abstract class WorkHttpAgent():WorkHttp(){
    protected var mServer: HttpServer? = null
    protected var mRouter:Router?=null
    protected var mDispatchWorkers = HashMap<String, String>()

    override fun start(config: String): WorkShop {
        super.start(config)

        var cfg= AnyJson(config)
        //设置基本配置
        var options= HttpServerOptions()
        options.setHost(cfg.get("host","0.0.0.0"))
                .setPort(cfg.get("port",80))
                .setSsl(cfg.get("ssl_on",false))


        //设置ssl相关的设置
        if(cfg.get("ssl_on",false)){
            options.setKeyStoreOptions(JksOptions().setPath(cfg.get("jks")).setPassword(cfg.get("jks_password")))
        }
        //创建server和路由
        mServer = Vertx().createHttpServer(options)
        mRouter = Router.router(Vertx())
        mRouter?.route()?.handler(CorsHandler.create("*").allowedMethod(HttpMethod.GET))//允许跨域
        mRouter?.route()?.handler(BodyHandler.create());
        setStdExceptionHandler(mRouter!!)
        //配置个性化路由
        addHandler(mRouter!!)

        //可以配置websocket
        setupWebsocketHandler(mServer!!,mRouter!!)


        /**
         * 加载自动转发的reqId列表
         */
        Msg(config).getArray("dispatch_workers", "req_id") { key, value ->
            mDispatchWorkers.put(key, (value as JsonObject).getString("work_id"))
        }

        //启动server
        mServer!!.requestHandler(mRouter).listen()



        return this
    }

  //  open fun checkToken(r: RoutingContext, block:(user: User)->Unit): User?{return null}
    abstract fun addHandler(router: Router)
    abstract fun setupWebsocketHandler(server:HttpServer,router: Router)


    fun setStdStaticHtml(path:String,rootDir:String){
        var handler=StaticHandler.create(rootDir)
                .setAllowRootFileSystemAccess(true)
                .setFilesReadOnly(true)
                .setMaxCacheSize(20000)//2倍默认
                //.setDirectoryListing(true)
                .setIndexPage("index.html")
        var p=if(path.endsWith("/"))"$path*" else "$path/*"
        mRouter?.route(p)?.handler(handler);
    }

    fun FILE(filename:String,fileInDisk:String){
        var handler=StaticHandler.create(fileInDisk)
        mRouter?.route(filename)?.handler(handler);
    }

    fun setBrowserHistoryPath(paths:Set<String>,to:String){
        paths.forEach {
            mRouter?.get(it)?.handler({ rc: RoutingContext -> rc.reroute(to) })
        }
    }

    fun setStdExceptionHandler(router: Router){
//        router.exceptionHandler { e ->
//            e.printStackTrace()
//            Log.e("WorkHttpAgent", e.message.toString())
//        }
    }

    /**
     * 将一个消息转发给另外一个Work处理并返回成功时的数据
     */
    protected fun rawForward(workId:String, msg:JsonObject, rc: RoutingContext, successHandler:(rc: RoutingContext, rsp:Msg)->Unit, failHandler:(rc: RoutingContext)->Unit, timeout:Long=2000L){
        send2Other(workId, msg, Handler<AsyncResult<Message<JsonObject>>>{

            if (it.succeeded()) {
                successHandler.invoke(rc,Msg(it.result().body()))
            } else {
                //no response
                failHandler.invoke(rc)
            }
        },timeout)
    }

    protected fun sqlForward(workId:String, msg:JsonObject, rc: RoutingContext,timeout:Long=2000L){
        jsonForward(workId,msg,rc,{o->
            o
        })
    }

    /**
     * 将http请求发给另外一个work
     */
    protected fun jsonForward(workId:String, msg:JsonObject, rc: RoutingContext,clip:(o:Msg)->Msg,timeout:Long=2000L){
        rawForward(workId,msg,rc,{rc,rsp->
            rc.response().replyJson(Msg().good(clip.invoke(rsp).delError()))
        },{rc->
            rc.response().replyJson((AnyJson().error(102,"no response from $workId")))
        })
    }

    /**
     * 将标准的http请求打包加上msgId和当前用户信息传递给指定的worker
     */
    protected fun stdForward(workId:String,msgId:String,msg:JsonObject, rc: RoutingContext,roles:Set<String> =setOf(), clip:(o:Msg)->Msg, timeout:Long=2000L){
        val token = getParamFromHeader<String>(rc,"token")
        val reqId = getParamByName(rc, "req_id")
        val json = getParamByName(rc, "json")

        var user= CusUser.getUser(token!!)
        if(null==user){
            replyJson(rc,Msg().error(Const.ErrUserNotLogin()))
            return
        }

        if(!roles.contains(user.role)){
            replyJson(rc,Msg().error(Const.ErrUserNoRight()))
            return

        }

        if(!mDispatchWorkers.containsKey(reqId)){
            replyJson(rc,Msg().error(104, "req_id is invalid"))
            return
        }


        var postWork=mDispatchWorkers.get(reqId)

        var msg=Msg(json)
                .msgId(msgId)
                .addUserFields(user)
                .add("json",json)


        jsonForward(postWork!!,msg,rc,clip,timeout)

    }





}