package com.runyu.worker




import com.runyu.com.runyu.worker.WorkerMaster
import com.runyu.http.HTTP_DEFAULT_TIMEOUT
import com.runyu.map.Cache
import com.runyu.sql.ExecptionSqlSorter
import com.runyu.sql.ExecptionSqlFilter
import com.runyu.sql.ValException
import com.runyu.std.AnyJson
import com.runyu.std.Const
import com.runyu.std.Log
import com.runyu.std.Msg
import io.vertx.core.AsyncResult
import io.vertx.core.DeploymentOptions
import io.vertx.core.Handler
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.eventbus.Message
import io.vertx.reactivex.core.shareddata.LocalMap
import java.sql.SQLIntegrityConstraintViolationException
import javax.cache.CacheException


/**
 * Created by sheng on 2017/5/31.
 */


/**
 * 标准微服务
 */
public abstract class WorkShop: WorkUnit(){
    val MAX_RETRY_COUNT=5
    var mDispatcher: Dispatcher?=null
    var mWorkId:String?=null
    /**
     * 初始化集群vertx实例,成功后去Config中心拿设置启动
     */
    override fun setup(config:String,instanceCount:Int): WorkShop {
        val options = DeploymentOptions().setInstances(instanceCount)
        //默认不是worker
        if(JsonObject(config).getBoolean("is_worker",false))
            options.setWorker(true)
        Vertx().deployVerticle(this,options)

        //直接创建agent
        setupAgents()

        start(config);

        return this;
    }


    /**
     * 标准的微服务处理方式
     */
    override fun start(config:String): WorkShop {
        mWorkId= AnyJson(config).getString("work_id")
        //注册消息处理
        Log.e("WorkShop","register handler for "+WorkerId())
        Vertx().eventBus().consumer<JsonObject>(WorkerId()) {
            message ->
            //Log.e(WorkerId(), "in : " + message.body())
            var msg=handleJson(message)
            Log.e(WorkerId(),"out: $msg !!")
            if(msg.isDone())
                message.reply(msg)
        };

        return this;
    }


    override fun shutdown(): WorkShop {
        mDispatcher=null

        return this;
    }

    override fun WorkerId(): String {
        return mWorkId!!
    }
    /**
     * 协助分发paper的助理
     */
    fun Dispatcher(): Dispatcher {
        return mDispatcher!!
    }

    /**
     * 设置根据MsgId进行处理的Agent,可以在这里替换不同的Dispatcher
     */
    open fun setupAgents(): WorkShop {
        mDispatcher= MsgIdDispatcher()
        return this
    }

    /**
     * 标准paper分发处理
     */
    override fun handleJson(paper: Message<JsonObject>): Msg {
        return Dispatcher()?.call(paper.body(),paper)
    }

    /**
     * 给自己发送paper
     */
    fun send2Self(outMsg:String){
        Vertx().eventBus().send(WorkerId(),outMsg)
    }

    /**
     * 发送给其它
     */
    fun send2Other(workId:String, outMsg:String,timeout:Long=HTTP_DEFAULT_TIMEOUT){
        var opt= DeliveryOptions().setSendTimeout(timeout)
        Vertx().eventBus().send(workId,outMsg,opt)
    }

    /**
     * 发送给其它
     */
    fun send2Other(workId:String, outMsg:JsonObject, callback: Handler<AsyncResult<Message<JsonObject>>>, timeout:Long=HTTP_DEFAULT_TIMEOUT){
        var opt= DeliveryOptions().setSendTimeout(timeout)
        Vertx().eventBus().request(workId,outMsg,opt,callback)
    }


    /**
     * 执行耗时任务
     */
    fun <T> executeBlocking(block: () -> T,afterDone:((success:Boolean,ret:T)->Unit)?=null) {
        Vertx().executeBlocking<T>({ future ->
            var res = block.invoke() as T
            future.complete(res)
        },
                false,
                { res ->
                    if (res.succeeded())
                        afterDone?.invoke(true,res.result())
                    else
                        afterDone?.invoke(false,res.result())
                })
    }


    /**
     * 设置配置接口
     */
    open fun setConfig(config:String):String{
        return "nothing done"
    }
    /**
     * 内部通信用共享map
     */
    fun Map(): LocalMap<String,Any> {
        return Vertx().sharedData().getLocalMap(WorkerId())
    }

    /**
     * 指定workId获取它的map
     */
    fun MapEx(mapName:String): LocalMap<String, Any> {
        return Vertx().sharedData().getLocalMap(mapName)
    }

    /**
     *
     */
    fun ShareMap(): LocalMap<String,Any> {
        return Vertx().sharedData().getLocalMap("all_workshop_share")
    }

    /**
     * 指定workId获取它的map
     */
    fun Map(mapName:String): Cache {
        return WorkerMaster.Map(mapName)
    }

    //////////////////////////////////////////////////////////////////////////////////////

    /**
     * 标准化一些异常处理，现在主要是处理校验异常
     */

    fun tryDo(message: Message<JsonObject>, block:()->Unit):Msg{
        try{
            block.invoke()
        }catch (e:IllegalArgumentException){
            e.printStackTrace()
            message.reply(Msg().error(Const.ErrFieldMissed()))
        }catch (e:ClassCastException){
            e.printStackTrace()
            message.reply(Msg().error(Const.ErrBadParameter()))
        }catch (e: ValException){
            e.printStackTrace()
            message.reply(Msg().error(Const.ErrValFail(e.message?:"校验错误")))
        }catch (e: ExecptionSqlFilter){
            e.printStackTrace()
            message.reply(Msg().error(Const.ErrBadFilter()))
        }catch (e: ExecptionSqlSorter){
            e.printStackTrace()
            message.reply(Msg().error(Const.ErrBadSorter()))
        }catch (e: SQLIntegrityConstraintViolationException){
            e.printStackTrace()
            message.reply(Msg().error(Const.ErrBadFilter()))
        }catch (e:KotlinNullPointerException){
            e.printStackTrace()
            message.reply(Msg().error(Const.ErrNullPointer()))
        }catch (e: CacheException){
            e.printStackTrace()
            message.reply(Msg().error(Const.ErrNullPointer()))
        }
        return Msg().notDone()
    }

}


