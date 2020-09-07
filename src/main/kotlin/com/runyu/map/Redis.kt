package com.runyu.map


import com.runyu.std.AnyJson
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

//private var redis_cfg=""

class RedisConnection {
    private var HOST = "127.0.0.1"
    private var PASSWORD = ""
    private var PORT = 6379
    private var DB=0
    private var MAX_ACTIVE = 1024
    private var MAX_IDLE = 200
    private var MAX_WAIT = 10000L
    var TIMEOUT=7200

    private var redis_cfg=""
    private var jedisPool: JedisPool? = null

    /*
     * 获取jedis实例
     * */
    //密码
    val jedis: Jedis?
        @Synchronized get() {
            try {
                if (jedisPool == null) {
                    initPool()
                }
                val jedis = jedisPool!!.resource
                jedis.auth(PASSWORD)
                jedis.select(DB)
                return jedis
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }

        }
    fun init(cfg:String):RedisConnection{
        redis_cfg=cfg
        return this
    }
    /*
     * 初始化redis连接池
     * */
    private fun initPool() {
        try {
            var cfg= AnyJson(redis_cfg)

            HOST=cfg.getString("host",HOST)
            PASSWORD=cfg.getString("password")
            DB=cfg.getInteger("database",DB)
            TIMEOUT = cfg.getInteger("key_timeout", TIMEOUT)//获取可用连接的最大等待时间

            val config = JedisPoolConfig()
            config.maxTotal =cfg.getInteger("max_active", MAX_ACTIVE)//最大连接数
            config.maxIdle = cfg.getInteger("max_idle",MAX_IDLE)//最大空闲连接数
            config.maxWaitMillis = cfg.getLong("max_wait",MAX_WAIT)//获取可用连接的最大等待时间

            jedisPool = JedisPool(config, HOST, PORT)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}


class Redis{
    lateinit var RedisConn:RedisConnection
    constructor(cfg:String){
        RedisConn=RedisConnection().init(cfg)
    }

    fun putObj(key: String, value: Any):Redis {
        val jedis = RedisConn.jedis
        jedis!!.set(key, AnyJson.toJson(value).toString())
        if(0!=RedisConn.TIMEOUT)
            jedis.expire(key, RedisConn.TIMEOUT);//设置时效实现timeoutInSeconds 秒
        jedis.close()
        return this
    }

    fun getObj(key: String, clazz: Class<*>): Any? {
        val jedis = RedisConn.jedis
        val value = jedis!!.get(key)
        jedis.close()
        if(null==value)
            return null
        else
            return AnyJson(value).mapTo(clazz)
    }

    fun put(key: String, value: String):Redis{
        val jedis = RedisConn.jedis
        jedis!!.set(key, value)
        if(0!=RedisConn.TIMEOUT)
            jedis.expire(key, RedisConn.TIMEOUT);//设置时效实现timeoutInSeconds 秒
        jedis.close()
        return this
    }

    fun get(key: String): String? {
        val jedis = RedisConn.jedis
        val value = jedis!!.get(key)
        jedis.close()
        return value
    }

    inline fun <reified T> get(key: String): T? {
        val jedis = RedisConn.jedis
        val value = jedis!!.get(key)
        jedis.close()
        return AnyJson(value).mapTo(T::class.java)
    }

    fun del(key:String):Redis{
        val jedis = RedisConn.jedis
        jedis!!.del(key)
        jedis.close()
        return this
    }

    fun containsKey(key:String):Boolean{
        val jedis = RedisConn.jedis
        var value=jedis!!.exists(key)
        jedis.close()
        return value
    }


    fun putMap(key:String,map:Map<String,String>,timeoutInSeconds:Int=0):Redis{
        val jedis = RedisConn.jedis
        jedis!!.hmset(key, map);
        if(0!=timeoutInSeconds)
            jedis.expire(key, timeoutInSeconds);//设置时效实现timeoutInSeconds 秒
        jedis.close()
        return this
    }

    fun getMapValue(key:String,field:String,timeoutInSeconds:Int=0):String{
        val jedis = RedisConn.jedis
        var value=jedis!!.hget(key,field)
        jedis.close()
        return value
    }

    inline fun getMap(key:String):MutableMap<String,String>{
        val jedis = RedisConn.jedis
        var value=jedis!!.hgetAll(key)
        jedis.close()
        return value as MutableMap<String,String>
    }
}
