package com.runyu.worker





import com.runyu.com.runyu.worker.WorkerMaster
import com.runyu.std.Msg
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.core.eventbus.Message


/**
 * Created by sheng on 2017/5/31.
 */

/**
 * 所有微服务的基类
 */

public abstract class WorkUnit: AbstractVerticle(){

    /**
     * 微服务启动接口
     * 调用后会自动寻找Config微服务，取到配置后自动调用start
     */
    abstract fun setup(config:String,instanceCount:Int=1): WorkUnit

    /**
     * 微服务配置和实际初始化后，开始等待paper并处理执行
     */
    abstract fun start(config:String): WorkUnit

    /**
     * 关闭这个微服务
     */
    abstract fun shutdown(): WorkUnit

    /**
     * 处理paper的总入口
     */
    open abstract fun handleJson(paper: Message<JsonObject>): Msg

    /**
     * 每个微服务必须有的唯一id
     */
    abstract fun WorkerId():String

    /**
     * 返回自己的worker
     */
    inline fun Vertx(): Vertx
    {
        return WorkerMaster.Vertx()
    }



}


public inline fun <T> T.with(body : T.() -> Unit) : T {
    body()
    return this
}

