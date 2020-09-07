package com.runyu.worker


import com.runyu.sql.DropKey
import com.runyu.sql.QueryResult
import com.runyu.std.*
import io.reactivex.Completable
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject

import io.vertx.ext.sql.ResultSet
import io.vertx.reactivex.core.eventbus.Message
import io.vertx.reactivex.ext.jdbc.JDBCClient
import io.vertx.reactivex.ext.sql.SQLClientHelper
import io.vertx.reactivex.ext.sql.SQLConnection
import java.lang.Exception


import java.sql.SQLException
import java.sql.SQLIntegrityConstraintViolationException
import java.sql.SQLSyntaxErrorException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit


public abstract class WorkSql : WorkShop() {
    protected var _LOG_RESULT=true
/*  config fields
    .put("max_pool_size", 3)
    .put("user", "root")
    .put("password", "root")
    .put("url", "jdbc:mysql://192.168.0.140:3306/devond_obd?useUnicode=true&amp;zeroDateTimeBehavior=convertToNull&amp;characterEncoding=UTF-8")
    .put("driver_class", "com.mysql.jdbc.Driver")
*/

    protected lateinit var mClient: JDBCClient
    protected var mTimeout=5000L

    override fun start(config: String): WorkShop {
        super.start(config)

        mClient = JDBCClient.create(Vertx(),AnyJson(config))

        Dispatcher().add(Const.MsgID.MSG_DB_SINGLE_QUERY, msgDbSingleQuery)
                .add(Const.MsgID.MSG_DB_SINGLE_ACTION, msgDbSingleAction)
                .add(Const.MsgID.MSG_DB_TRANSACTION, msgDbTransaction)
                .add(Const.MsgID.MSG_DB_QUERIES, msgDbQueries)

        return this;
    }


    protected var msgDbQueries: (key: String, msg: JsonObject, message: Message<JsonObject>) -> Msg = { key, msg, message ->
        var keys = Msg(msg).dbBatchKey()
        var sql = Msg(msg).dbBatchCmd()
        runQueries(keys,sql,DropKey(),{ res->
            message.reply(res)
        })
        Msg().notDone()
    }

    protected var msgDbTransaction: (key: String, msg: JsonObject, message: Message<JsonObject>) -> Msg = { key, msg, message ->
        var sql = Msg(msg).dbBatchCmd()
        transaction(sql, message)
    }

    protected var msgDbSingleQuery: (key: String, msg: JsonObject, message: Message<JsonObject>) -> Msg = { key, msg, message ->
        var sql = Msg(msg).dbCmd()
        singleQuery(sql,message)
    }

    protected var msgDbSingleAction: (key: String, msg: JsonObject, message: Message<JsonObject>) -> Msg = { key, msg, message ->
        var sql = Msg(msg).dbCmd()
        singleAction(sql,message)
    }


    /**
     * 标准查询列表数据，singleQuery上做了一些特殊处理，如查询出记录总数
     */
    fun getQuery(sql: String, message: Message<JsonObject>?,dataBlock:((msg:Msg)->JsonObject)?=null,timeout: Long = 20000): Msg{
        singleQuery(sql,{json->
            //  dropKeys.forEach { json.remove(it) }
            //计算添加分页数据
            var qr=QueryResult(json)
            if(qr.numOfRow()>0){
                json.mergeIn(qr.first())
            }
            //从每行里删除分页原始数据
            var out=qr.clean()
            json.remove("numRows")
            json.remove("rows")
            json.remove("cOUNT")
            json.remove("pAGESIZE")
            json.remove("pAGEINDEX")
            out=dataBlock?.invoke(Msg(out))?:out
            message?.reply(out)
        })
        return Msg().notDone()
    }


    /**
     * 标准查询列表数据，singleQuery上做了一些特殊处理，如查询出记录总数
     */
    fun listQuery(sql: String, message: Message<JsonObject>,dataBlock:((msg:Msg)->JsonObject)?=null,timeout: Long = 20000): Msg{
        singleQuery(sql,{json->
          //  dropKeys.forEach { json.remove(it) }
            //计算添加分页数据
            var qr=QueryResult(json)

            var pageSize=qr.first()?.getInteger("pAGESIZE")?:1
            var total=qr.first()?.getInteger("cOUNT")?:0
            var pageIndex=qr.first()?.getInteger("pAGEINDEX")?:0

            json.put("pageSize",pageSize)
            json.put("pageIndex",pageIndex)
            json.put("total",total)
            //从每行里删除分页原始数据

            var out=dataBlock?.invoke(Msg(qr.clean()))?:qr.clean()
        //    var out=dataBlock?.invoke(Msg(json))?:json
            message.reply(out)
        })
        return Msg().notDone()
    }


    /**
     * 执行单独一句sql查询并返回结果
     */
    fun singleQuery(sql: String, message: Message<JsonObject>,dropKeys:DropKey=DropKey(),dataBlock:((msg:Msg)->JsonObject)?=null,timeout: Long = 20000): Msg{
        singleQuery(sql,{json->
            dropKeys.forEach { json.remove(it) }
            var out=dataBlock?.invoke(Msg(json))?:json
            message.reply(out)
        })
        return Msg().notDone()
    }




    /**
     * 执行单独一句sql查询并返回结果
     */
    open fun singleQuery(sql: String, block:(msg:JsonObject)->Unit,timeout: Long = 20000,dbName:String?=null): Msg{
        if(_LOG_RESULT)
            Log.e("singleQuery","sql=$sql")

        Vertx().executeBlocking<JsonObject>({ future ->
            var res:ResultSet?=null
            var outcome = CompletableFuture.supplyAsync {
                dbName?.let{
                    mClient.rxCall("use $dbName").blockingGet()
                }
                res = mClient.rxQuery(sql).blockingGet()
            }
            outcome.get(timeout, TimeUnit.MILLISECONDS)
            future.complete(res!!.toJson().putNull("results"))
        },
                false,
                { res ->
                    var out=JsonObject()
                    if (res.succeeded()) {
                        out=Msg(res.result()).error(0).put("sql",sql)
                    }
                    else {
                        out=Msg().error(getError(res.cause()))
                    }
                    block.invoke(out)

                    if(_LOG_RESULT)
                        Log.e("singleQuery",out)
                })
        return Msg().notDone()
    }


    /**
     * 执行单独一句sql，不要求返回结果
     */
    fun singleAction(sql: String, message: Message<JsonObject>, dropKeys:DropKey= DropKey(), dataBlock:((msg:Msg)->JsonObject)?=null, timeout: Long = 20000): Msg{
        singleAction(sql,{json->
            dropKeys.forEach { json.remove(it) }
            var out=dataBlock?.invoke(Msg(json))?:json
            message.reply(out)
        })
        return Msg().notDone()
    }



    /**
     * 执行单独一句sql，不要求返回结果
     */
    fun singleAction(sql: String, block:(msg:JsonObject)->Unit,timeout: Long = 20000): Msg{
        mClient.rxQuery(sql)
                .subscribe({
                    // Send JSON to the mClient
                    var out = Msg().add("code", 0)
                    block.invoke(out)
                    if (_LOG_RESULT)
                        Log.e("singleAction", out)
                }, { t ->
                    // Send error to the mClient
                    var out = Msg().error(getError(t))
                    block.invoke(out)
                    if (_LOG_RESULT)
                        Log.e("singleAction", out)
                })
        return Msg().notDone()
    }




    @Throws(SQLException::class)
    fun toJsonArray(rs: io.vertx.ext.sql.ResultSet): JsonArray {

        var arr = JsonArray()
        if (null != rs.results) {
            rs.results.forEach { item ->
                arr.add(item)
            }
        }
        return arr
    }

    fun Completable.then(sqlConnection: SQLConnection, sql: Array<String>, startIndex: Int): Completable {
        if(startIndex>=sql.size)
            return this
        else
            return this.andThen(sqlConnection.rxExecute(sql[startIndex]))
                    .then(sqlConnection,sql,startIndex+1)
    }





    /**
     * 批量处理sql语句，失败即回滚
     */

    fun transaction(sql: Array<String>, message: Message<JsonObject>,dataBlock:((msg:Msg)->JsonObject)?=null,dropKeys:DropKey=DropKey(),timeout: Long = 20000): Msg{
        transaction(sql,{json->
            dropKeys.forEach { json.remove(it) }
            var out=dataBlock?.invoke(Msg(json))?:json
            message.reply(out)
        },timeout)
        return Msg().notDone()
    }
    /**
     * 批量处理sql语句，失败即回滚
     */
    fun transaction(sql: Array<String>,block:(msg:JsonObject)->Unit,timeout: Long = 20000): Msg{

        SQLClientHelper.inTransactionSingle(mClient, { sqlConnection ->
            var c= if(1==sql.size) sqlConnection.rxExecute(sql[0])  else   sqlConnection.rxExecute(sql[0]).then(sqlConnection, sql, 1)
            c.andThen(sqlConnection.rxQuery("select ROW_COUNT() as count").map(ResultSet::getResults))
        }).map { rows ->
            // Transform DB rows into a client-friendly JSON object
            //根据查询确定是否有数据被影响，没有的话抛出异常
            var affectedRows=rows[0].first() as Long
            if(0L==affectedRows) {
                throw Exception("业务处理0行数据")
            }
        }.subscribe({ json ->
            // Send JSON to the mClient
            var out = Msg().add("code", 0)
            block.invoke(out)
            if(_LOG_RESULT)
                Log.e("transaction",out)

        }, {t ->
            // Send error to the mClient
            var out = Msg().error(getError(t))
            block.invoke(out)
            if(_LOG_RESULT)
                Log.e("transaction",out)
        })

        return Msg().notDone()
    }


    fun multiQueries(sql: Array<String>, message: Message<JsonObject>,dataBlock:((msg:Msg)->JsonObject)?=null,dropKeys:DropKey=DropKey(),timeout: Long = 20000): Msg{
        multiQueries(sql,{json->
            dropKeys.forEach { json.remove(it) }
            var out=dataBlock?.invoke(Msg(json))?:json
            message.reply(out)
        },timeout)
        return Msg().notDone()
    }


    fun multiQueries(sql: Array<String>,block:(msg:JsonObject)->Unit,timeout: Long = 20000): Msg{
        var lastSql=sql.last()
        var sqlEx=sql.dropLast(1).toTypedArray()
        SQLClientHelper.inTransactionSingle(mClient, { sqlConnection ->
            return@inTransactionSingle sqlConnection.rxExecute(sqlEx[0]).then(sqlConnection, sqlEx, 1)
                .andThen(sqlConnection.rxQuery(lastSql)).cache()
        }).map { raw ->
            Msg().add("rows",raw.rows).add("numRows",raw.numRows).add("columns",raw.columnNames)
        }.subscribe({ json ->
            json as JsonObject
            // Send JSON to the mClient
            var out = Msg(json).add("code", 0)
            block.invoke(out)
            if(_LOG_RESULT)
                Log.e("transaction",out)

        }, {t ->
            // Send error to the mClient
            var out = Msg().error(getError(t))
            block.invoke(out)
            if(_LOG_RESULT)
                Log.e("transaction",out)
        })

        return Msg().notDone()
    }

    fun getError(e:Throwable):SysErr{
        if(e is SQLIntegrityConstraintViolationException){
            return SysErr(339,"试图插入重复的数据内容或错误外键:"+e.message)
        }else if(e is SQLSyntaxErrorException){
            return SysErr(1064,"SQL 语法错误:"+e.message)
        }

        return SysErr(104,"SQL 执行异常:" + e.message)
    }

    /**
     * 同步批量运行sql查询语句，将结果放入对应的key中,将rows中的内容取出然后reply出去
     */
    fun runQueries(keys: Array<String>, sql: Array<String>,message: Message<JsonObject>,dropKeys:DropKey=DropKey(),dataBlock:((msg:Msg)->JsonObject)?=null, timeout:Long=20000L):Msg {
        runQueries(keys,sql,dropKeys,{json->
            var out=dataBlock?.invoke(Msg(json))?:json
            message.reply(out)
        },timeout)
        return Msg().notDone()
    }

    /**
     * 同步批量运行sql查询语句，将结果放入对应的key中
     */
    fun blockQueries(keys: Array<String>, sql: Array<String>,dropKeys:Set<String>,timeout: Long = 20000): JsonObject {
        if (keys.isEmpty() || sql.isEmpty() || keys.size != sql.size)
            return Msg().error(ErrByParam("bad parameters"))

        if(_LOG_RESULT)
            sql.forEach { Log.e("executeQuery", it) }

        var outcome = CompletableFuture.supplyAsync {
            var res = mClient.rxQuery(sql[0]).blockingGet()
            var out=res.toJson()
            dropKeys.plus("results").forEach { out.remove(it)}
            AnyJson().put(keys[0], out)
        }
        sql.forEachIndexed { idx, it ->
            if (0 != idx) {
                outcome=outcome.thenApply { json ->
                    var res = mClient.rxQuery(sql[idx]).blockingGet()
                    var out=res.toJson().putNull("results").put("sql",sql[idx])
                    dropKeys.forEach { out.remove(it)}

                    AnyJson(json).put(keys[idx], out)
                }
            }
        }
        return outcome.get(timeout, TimeUnit.MILLISECONDS)
    }

    /**
     * 异步批量运行sql查询语句，将结果放入对应的key中
     */
    fun runQueries(keys: Array<String>, sql: Array<String>, dropKeys:Set<String>,block: (json: JsonObject) -> Unit, timeout:Long=20000L) {
        Vertx().executeBlocking<JsonObject>({ future ->
            var res = blockQueries(keys,sql,dropKeys,timeout)
            future.complete(res)
        },
                false,
                { res ->
                    var out=JsonObject()
                    if (res.succeeded())
                        out=Msg(res.result()).error(0)
                    else
                        out=Msg().error(getError(res.cause()))

                    block.invoke(out)
                    if(_LOG_RESULT)
                        Log.e("runQueries",out)
                })
    }


    /**
     * 先校验数据库中是否存在相关的id等，checks必须类似如下格式
     * "select count(blog_id) as count from blogs where blog_id='$reportId'"
     */
    fun checkRunTransaction(checks: Array<String>,sql: Array<String>, message: Message<JsonObject>,dropKeys:DropKey=DropKey(),dataBlock:((msg:Msg)->JsonObject)?=null,timeout: Long = 20000): Msg{
        checkRunTransaction(checks,sql,{json->
            dropKeys.forEach { json.remove(it) }
            var out=dataBlock?.invoke(Msg(json))?:json
            message.reply(out)
        },timeout)
        return Msg().notDone()
    }


    public fun checkRunTransaction(checks: Array<String>, sql: Array<String>, block: (json: JsonObject) -> Unit, timeout:Long=20000L) {
        var keys=arrayOf<String>()
        checks.forEachIndexed({idx,it->keys=keys.plus("key$idx")})

        runQueries(keys,checks,DropKey(),{ json->
            if(0==Msg(json).errCode()){
                var index=checkKeys(json,keys)
                if(-1==index) {
                    transaction(sql, { j ->
                        block.invoke(j)//直接返回业务处理结果
                    }, timeout)
                }else{
                    block.invoke(Msg().error(Const.ErrBadCheck("key$index")))//返回校验失败
                }
            }
        },timeout)
    }

    fun checkKeys(json:JsonObject,keys: Array<String>):Int{
        var ret=-1
        for(i in 0..keys.size-1){
            var j=json.getJsonObject("key$i")
            var row0=j.getJsonArray("rows").getJsonObject(0)
            if(0==row0.getInteger("count")){
                ret=i
                break
            }
        }
        return ret
    }


    fun <T> getRowItemValue(json:JsonObject,key:String,rowNo:Int=0):T?{
        if(0==json.getInteger("numRows"))return null

        var row=json.getJsonArray("rows").getJsonObject(rowNo)
        return row.getValue(key) as T
    }

    fun takeRows(json: JsonObject,keys: Array<String>): JsonObject {
        json.fieldNames().forEach {
            if(keys.contains(it)){
                json.put(it,json.getJsonObject(it).getJsonArray("rows"))
            }
        }
        return json
    }
/*
List<String> batch = new ArrayList<>();
batch.add("INSERT INTO emp (NAME) VALUES ('JOE')");
batch.add("INSERT INTO emp (NAME) VALUES ('JANE')");

connection.batch(batch, res -> {
  if (res.succeeded()) {
    List<Integer> result = res.result();
  } else {
    // Failed!
  }
});
 */








}