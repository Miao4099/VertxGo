package com.runyu.mq

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import com.rabbitmq.client.ConnectionFactory



class MqTx{
    private var mExchangeName=""
    private var mChannel: Channel?=null
    private var mConn:Connection?=null




    fun build(host:String,user:String,password:String,exchangeName:String,style:String="topic"):MqTx{
        val factory = ConnectionFactory()
        factory.username = user
        factory.password = password
        //设置 RabbitMQ 地址
        factory.host = host
        //建立到代理服务器到连接
        val mConn = factory.newConnection()
        //获得信道
        mChannel = mConn.createChannel()
        //声明交换器
        mExchangeName=exchangeName
        mChannel!!.exchangeDeclare(exchangeName, style, true)

        return this
    }


    fun tx(routingKey:String, msg:JsonObject) {
        //发布消息
        mChannel?.basicPublish(mExchangeName, routingKey, null, msg.toString().toByteArray())
    }

    fun close(){
        mChannel?.close()
        mConn?.close()
    }
}