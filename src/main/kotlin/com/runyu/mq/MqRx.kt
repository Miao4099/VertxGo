package com.runyu.mq

import com.rabbitmq.client.*
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import java.io.IOException
import java.nio.charset.Charset


class MqRx{
    private var mExchangeName=""
    private var mChannel: Channel?=null
    private var mConn: Connection?=null
    private var mQueueName=""
    private var mHandler:((msg:String)->Unit)?=null

    fun build(host:String,user:String,password:String,exchangeName:String,routingKey:String,style:String="topic"):MqRx{
        val factory = ConnectionFactory()
        factory.username = user
        factory.password = password
        factory.host = host
        //建立到代理服务器到连接
        mConn = factory.newConnection()
        //获得信道
        mChannel = mConn!!.createChannel()
        //声明交换器
        mExchangeName = exchangeName
        mChannel!!.exchangeDeclare(exchangeName, style, true)
        //声明队列
        mQueueName = mChannel!!.queueDeclare().queue
        //绑定队列，通过键 hola 将队列和交换器绑定起来
        mChannel!!.queueBind(mQueueName, exchangeName, routingKey)

        return this
    }

    fun setHandler(handler:(msg:String)->Unit):MqRx{
        mHandler=handler
        return this
    }

    fun rx():MqRx{
        val consumer = object : DefaultConsumer(mChannel) {
            @Throws(IOException::class)
            override fun handleDelivery(consumerTag: String, envelope: Envelope, properties: AMQP.BasicProperties, body: ByteArray) {
                var message:String? = String(body, Charset.forName("UTF-8"))


                try {
                    mHandler?.invoke(message!!)
                } finally {
                    message=null
                }
            }
        }
        val autoAck = true // acknowledgment is covered below
        mChannel?.basicConsume(mQueueName, autoAck, consumer)
        return this
    }

    fun close(){
        mChannel?.close()
        mConn?.close()
    }




}