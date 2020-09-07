package com.runyu.com.runyu.worker

import com.hazelcast.config.Config
import com.hazelcast.config.GroupConfig
import com.runyu.map.Cache
import com.runyu.std.AnyJson
import com.runyu.std.Log
import com.runyu.worker.WorkUnit
import io.vertx.core.VertxOptions
import io.vertx.core.json.JsonObject
import io.vertx.core.spi.cluster.ClusterManager
import io.vertx.reactivex.core.Vertx
import io.vertx.spi.cluster.ignite.IgniteClusterManager
import org.apache.ignite.Ignite
import org.apache.ignite.IgniteCache
import org.apache.ignite.Ignition
import org.apache.ignite.cache.query.SqlFieldsQuery
import org.apache.ignite.cache.query.annotations.QuerySqlField
import java.io.File
import java.io.Serializable
import java.util.*
import java.util.concurrent.ConcurrentHashMap



object WorkerMaster{
    private var mWorkerSqlName:String?=null
    private var mVertx: Vertx?=null
    private var mIgnite: Ignite?=null
    private var mCfg:String?=null
    private var mUserTokenTimeout=3600L
    private var mMapList:ConcurrentHashMap<String,Cache>? = null

    fun readCfg(cfgName:String):String{
        val currentDir = getConfigPath()
        System.out.println("readCfg from "+currentDir)
        val file = File(currentDir, cfgName+".json")
        var config="{}"
        try {
            config = file.readText()
        }catch (e:Exception){
            e.printStackTrace()
        }
        return config
    }

    private fun getConfigPath():String{
        return System.getProperty("user.dir") + "/config"
    }

    public fun Vertx(): Vertx {
        return mVertx!!
    }

    public fun Ignite(): Ignite {
        return mIgnite!!
    }

    fun getCfg(workName:String):String{
        return (AnyJson(mCfg!!).get(workName) as JsonObject).toString()
    }

    fun setWorkerSqlName(name:String){
        mWorkerSqlName=(AnyJson(mCfg!!).get(name) as JsonObject).getString("work_id")
    }

    fun getWorkerSqlName():String{
        return mWorkerSqlName!!
    }


    fun initSystem(cfgName:String, onInitWorks:(v:Vertx, cfg: JsonObject)->Unit){
        System.out.println("initSystem $cfgName.....v100..................");

        mCfg=readCfg(cfgName)
        var cfg= AnyJson(mCfg!!)

        System.out.println("initSystem init log..................");
        var logConfigName=cfg.get("cfg_log","config/log/log4j2.xml")
        Log.reload(logConfigName)
        System.out.println("initSystem init log end..................");

        val cfgMgr = Config()

        val clusterManager: ClusterManager = IgniteClusterManager()

        // 加入组的配置，防止广播环境下，负载串到别的开发机中
        val group = GroupConfig()
                .setName(cfg.get("group_name"))
        cfgMgr.groupConfig=group


        var opt= VertxOptions()
                .setClusterManager(clusterManager)
                .setHAGroup(cfg.get("group_name"))
                .setHAEnabled(true)
                .setWorkerPoolSize(40)


//        val uuid = clusterManager.nodeID
//        var ignite = Ignition.ignite(UUID.fromString(uuid))

        mUserTokenTimeout=cfg.getLong("user_token_timeout",3600)

        Vertx.clusteredVertx(opt) { res ->
            Log.e("initSystem","--------$cfgName  success----------")
            mVertx= res.result()
      //      loadWorkers(cfg)
            //map初始化必须放在work初始化之前
            mMapList= ConcurrentHashMap<String,Cache>()

            val uuid = clusterManager.nodeId
            mIgnite = Ignition.ignite(UUID.fromString(uuid))

            onInitWorks(mVertx!!,cfg)
        }

    }

    fun createWorkFromName(fqn:String):WorkUnit{
        return Class.forName(fqn).kotlin.objectInstance as WorkUnit
    }

    fun loadWorkers(cfg:AnyJson){
        cfg.getJsonArray("workers").forEach {
            var o=it as JsonObject
            var name=o.getString("class_name")

            var worker=createWorkFromName(name)
            worker.setup(o.toString())
        }
    }

    public fun getUserTokenTimeout():Long{
        return mUserTokenTimeout
    }

    public fun Map(name:String):Cache{
        if(!mMapList!!.containsKey(name)) {
            mMapList!!.put(name,Cache(name))
        }
        return mMapList!!.get(name)!!

    }

}