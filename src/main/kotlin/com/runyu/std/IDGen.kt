package com.runyu.std

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class IDGen @JvmOverloads constructor(private val workerId: Long = 0L, private val datacenterId: Long = 0L) {
    private var sequence = 0L
    private val twepoch = 1288834974657L                              //  Thu, 04 Nov 2010 01:42:54 GMT
    private val workerIdBits = 5L                                     //  节点ID长度
    private val datacenterIdBits = 5L                                 //  数据中心ID长度
    private val maxWorkerId = -1L xor (-1L shl workerIdBits.toInt())             //  最大支持机器节点数0~31，一共32个
    private val maxDatacenterId = -1L xor (-1L shl datacenterIdBits.toInt())     //  最大支持数据中心节点数0~31，一共32个
    private val sequenceBits = 12L                                    //  序列号12位
    private val workerIdShift = sequenceBits                          //  机器节点左移12位
    private val datacenterIdShift = sequenceBits + workerIdBits       //  数据中心节点左移17位
    private val timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits //  时间毫秒数左移22位
    private val sequenceMask = -1L xor (-1L shl sequenceBits.toInt())                          //  4095
    private var lastTimestamp = -1L

    private object IdGenHolder {
        val instance = IDGen()
    }

    init {
        if (workerId > maxWorkerId || workerId < 0) {
            throw IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId))
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId))
        }
    }

    @Synchronized
    fun nextId(): Long {
        //获取当前毫秒数
        var timestamp = timeGen()
        //如果服务器时间有问题(时钟后退) 报错。
        if (timestamp < lastTimestamp) {
            throw RuntimeException(String.format(
                    "Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp))
        }
        //如果上次生成时间和当前时间相同,在同一毫秒内
        if (lastTimestamp == timestamp) {
            //sequence自增，因为sequence只有12bit，所以和sequenceMask相与一下，去掉高位
            sequence = sequence + 1 and sequenceMask
            //判断是否溢出,也就是每毫秒内超过4095，当为4096时，与sequenceMask相与，sequence就等于0
            if (sequence == 0L) {
                //自旋等待到下一毫秒
                timestamp = tilNextMillis(lastTimestamp)
            }
        } else {
            //如果和上次生成时间不同,重置sequence，就是下一毫秒开始，sequence计数重新从0开始累加
            sequence = 0L
        }
        lastTimestamp = timestamp
        // 最后按照规则拼出ID。
        // 000000000000000000000000000000000000000000  00000            00000       000000000000
        // time                                       datacenterId   workerId    sequence
        return (timestamp - twepoch shl timestampLeftShift.toInt() or (datacenterId shl datacenterIdShift.toInt())
                or (workerId shl workerIdShift.toInt()) or sequence)
    }

    protected fun tilNextMillis(lastTimestamp: Long): Long {
        var timestamp = timeGen()
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen()
        }
        return timestamp
    }

    protected fun timeGen(): Long {
        return System.currentTimeMillis()
    }

    companion object {

        fun get(): IDGen {
            return IdGenHolder.instance
        }

        fun getId():Long{
            return get().nextId()
        }

        private fun getStrId():String{
            return ""+get().nextId()
        }

        fun md5(text: String): String {
            try {
                //获取md5加密对象
                val instance: MessageDigest = MessageDigest.getInstance("MD5")
                //对字符串加密，返回字节数组
                val digest:ByteArray = instance.digest(text.toByteArray())
                var sb : StringBuffer = StringBuffer()
                for (b in digest) {
                    //获取低八位有效值
                    var i :Int = b.toInt() and 0xff
                    //将整数转化为16进制
                    var hexString = Integer.toHexString(i)
                    if (hexString.length < 2) {
                        //如果是一位的话，补0
                        hexString = "0" + hexString
                    }
                    sb.append(hexString)
                }
                return sb.toString()

            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }

            return ""
        }

        fun strId(): String {
            return md5(getStrId())
        }

        fun toHex(byteArray: ByteArray): String {
            val result = with(StringBuilder()) {
                byteArray.forEach {
                    val hex = it.toInt() and (0xFF)
                    val hexStr = Integer.toHexString(hex)
                    if (hexStr.length == 1) {
                        this.append("0").append(hexStr)
                    } else {
                        this.append(hexStr)
                    }
                }
                this.toString()
            }
            //转成16进制后是32字节
            return result
        }

        fun sha1(str:String): String {
            val digest = MessageDigest.getInstance("SHA-1")
            val result = digest.digest(str.toByteArray())
            return toHex(result)
        }

        fun sha256(str:String): String {
            val digest = MessageDigest.getInstance("SHA-256")
            val result = digest.digest(str.toByteArray())
            return toHex(result)
        }
    }
}
