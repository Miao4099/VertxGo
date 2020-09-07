package com.runyu.sql


import com.runyu.std.IDGen
import com.runyu.std.Log
import com.runyu.std.PageInfo
import com.runyu.std.jsonGet
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject


class SqlCases(var pairs:Map<String,Any>, var displayName:String){
    fun build(fn:String):String {
        var keyValues = ""
        pairs.forEach { value, name->
            if(value.isNullOrEmpty())
                keyValues+="else ${getValue(name)} "
            else
                keyValues+="when ${getValue(value)} then ${getValue(name)} "
        }
        return "(case $fn $keyValues end) $displayName"
    }

}

fun getValue(v:Any?):String{
    if(null==v)
        return "null"


    if(v is String || v is ArrayList<*>|| v is JsonObject || v is JsonArray)
        return "'$v'"
    else
        return v.toString()
}

class SqlMaker(var tableName:String){
    data class Field(
        var name:String,
        var value:Any?,
        var order:Int
    )
    protected var mFiledIndex=0
    protected var mFieldInfos=HashMap<String,Field>()
    protected var mFieldsAlias=HashMap<String,String>()
    protected var mFields=HashMap<String,Any?>()
    protected var mFrom:String?=null
    protected var mValues=arrayOf<String>()
    protected var mIgnoreFields=setOf<String>()

    fun setAlias(fl:Map<String,String>):SqlMaker{
        mFieldsAlias.clear()
        //key value 倒转 为 DB filed name:alias name
        fl.forEach{mFieldsAlias.put(it.value,it.key)}
        return this
    }

    inline fun set(key:String,value:Any?=null,required:Boolean=false):SqlMaker{
        if(required&&null==value)
            throw IllegalArgumentException("$key can't be null")

        mFields.put(key,value)
        mFieldInfos.put(key,Field(key,value,mFiledIndex++))
        return this
    }

    inline fun set(key:String,value:Any?):SqlMaker{
        mFields.put(key,value)
        mFieldInfos.put(key,Field(key,value,mFiledIndex++))
        return this
    }

    inline fun set(key:String,value:Any?,validator: Validator):SqlMaker{
        var newValue=validator.validate(value)
        mFields.put(key,newValue)
        mFieldInfos.put(key,Field(key,newValue,mFiledIndex++))
        return this
    }

    inline fun <T> set(key:String,msg:JsonObject,validator: Validator?=null):SqlMaker{
        //先尝试DB field name，找不到再换alias name，不行的话，再换回DB field name
        var value=msg.jsonGet<T>(key,msg.jsonGet<T>(mFieldsAlias[key]?:key) as T) as T
        var newValue=validator?.validate(value)?:value
        mFields.put(key,newValue)
        mFieldInfos.put(key,Field(key,newValue,mFiledIndex++))
        return this
    }

    fun set(values: JsonObject):SqlMaker{
        values.fieldNames().forEach {
            mFields.put(it,values.getValue(it))
            mFieldInfos.put(it,Field(it,values.getValue(it),mFiledIndex++))
        }
        return this
    }

    fun set(values: HashMap<String,Any>):SqlMaker{
        values.forEach {
            mFields.put(it.key,getValue(it.value))
            mFieldInfos.put(it.key,Field(it.key,getValue(it.value),mFiledIndex++))
        }
        return this
    }

    fun set(values: JsonArray,block:(json:JsonObject)->JsonObject):SqlMaker{
        values.forEach {
            var j=block(it as JsonObject)
            set(j)
        }
        return this
    }

//    fun set(key:String,cases: SqlCases):SqlMaker{
//        mFields.put(key,cases)
//        return this
//    }

    //这个还没改
    fun add(key:String,value:Any):SqlMaker{
        if(mFields.containsKey(key)&& mFields[key] is List<*>){
            (mFields[key] as ArrayList<Any>).add(value!!)
        }else{
            var l=arrayListOf<Any>()
            l.add(value!!)
            mFields.put(key, l)
        }
        return this
    }

    /**
     * 压入多行的值
     */
    fun push(vararg values:Any?):SqlMaker{
        var row=""
        values.forEach { row+=","+getValue(it) }
        row="(${row.drop(1)})"
        mValues=mValues.plus(row)
        return this
    }

    fun from(source:String):SqlMaker{
        mFrom=source
        Log.e("SqlMaker from:",source)
        return this
    }


    private fun getLimit(pageSize:Int,pageIndex:Int):String{
        if(pageSize!=0&&pageIndex>=0) {
            var startIdx = (pageIndex-1) * pageSize
            return " limit $startIdx,$pageSize"
        }
        return ""
    }

    fun getFieldByFactor(fn:String):String{
        if(mFields.get(fn) is SqlCases){
            return (mFields.get(fn) as SqlCases).build(fn)
        }else if(mFieldsAlias.containsKey(fn))
            return "$fn as "+mFieldsAlias.get(fn)
        else
            return fn
    }

    /**
     * set key,但是没有value的会作为查询结果
     * set key,但是有value的会作为比较条件和查询结果
     */
    fun getList(where:String="",orderBy:String="",pageIndex:Int=0,pageSize:Int=0):String{
        var us=""
        var filters=""
        mFields.forEach{

            us += ",${getFieldByFactor(it.key)}"

            //如果不空，set的字段作为过滤条件
            it.value?.apply{
                filters+=" AND ${it.key}= ${getValue(it.value)}"
            }
        }
        us=us.drop(1)
        filters=filters.drop(4)

        if(us.isNullOrEmpty())
            us="*"

        var limit=getLimit(pageSize,pageIndex)

        if(where.isNotEmpty()&& filters.isNotEmpty())
            filters="($where) AND ($filters)"
        else if(where.isNullOrEmpty()&&filters.isNotEmpty())
            filters="where ($filters)"
        else
            filters=where

        //return "select ${us} from $tableName $filters $orderBy $limit"
        return "select ${getListFields(us,pageSize,pageIndex)} from ${getListTableName(filters)} $filters $orderBy $limit"
    }


    fun ingoreFilter(fields:Set<String>):SqlMaker{
        mIgnoreFields=mIgnoreFields.plus(fields)
        return this
    }

    fun getList(where:String="",orderBy:String="",pi:PageInfo):String{
        return getList(where,orderBy,pi.pageIndex,pi.pageSize)
    }

    fun getList(where:QueryFilter?=null,orderBy:QuerySorter,pageIndex:Int=0,pageSize:Int=0):String{
        var us=""
        var filters=where?.build(mIgnoreFields)?:""
       // if(mIngoreFilter) filters="" //如果要求忽略过滤器，如需要过滤字段为空的情况

        mFields.forEach{
            us += ",${getFieldByFactor(it.key)}"
        }
        us=us.drop(1)

        if(us.isNullOrEmpty())
            us="*"

        var limit=getLimit(pageSize,pageIndex)

        var order=orderBy.build()

        if(null!=mFrom)
            return "select ${getListFieldsForCustomFrom(us,pageSize,pageIndex)} from (${getListTableNameFromCustomFrom(mFrom,filters)}) $filters ${order} $limit"
        else
            return "select ${getListFields(us,pageSize,pageIndex)} from ${getListTableName(filters)} $filters ${order} $limit"
    }

    fun getList(where:QueryFilter?=null,orderBy:QuerySorter,pi:PageInfo):String{
        return getList(where,orderBy, pi.pageIndex,pi.pageSize)
    }

    private fun getListFieldsForCustomFrom(us:String,pageSize:Int,pageIndex:Int):String{
        return "${us},Y.cOUNT,$pageSize as pAGESIZE,$pageIndex as pAGEINDEX"
    }

    private fun getListFields(us:String,pageSize:Int,pageIndex:Int):String{
        return "${us},b.*,$pageSize as pAGESIZE,$pageIndex as pAGEINDEX"
    }

    private fun getListTableNameFromCustomFrom(from:String?,filters:String):String{
        return "($from) X,"+
                "    (" +
                "        SELECT" +
                "            count(1)  as cOUNT" +
                "        FROM" +
                "            ($from) X " + filters +
                "    ) Y"

    }
    private fun getListTableName(filters:String):String{
        return "( $tableName a," +
                "    (" +
                "        SELECT" +
                "            count(1)  as cOUNT" +
                "        FROM" +
                "            $tableName " + filters +
                "    ) b" +
                ")"
    }

    fun getInsert():String{
        var ms=""
        var vs=""
        var us=""
        mFields.forEach{
            ms += ",${it.key}"
            var value=if(null!=it.value) "${getValue(it.value!!)?:null}" else "null"
            vs += ",$value"
            us += ",${it.key}=$value"
        }
        ms=ms.drop(1)
        vs=vs.drop(1)
        us=us.drop(1)
        return "INSERT INTO $tableName ($ms) VALUES ($vs)"
    }


    fun getWhere():String{
        var ms=""
        var vs=""
        var us=""
        mFields.forEach{
            var value=if(null!=it.value) "${getValue(it.value!!)?:null}" else "null"
            us += " and ${it.key}=$value"
        }

        us=us.replaceFirst("and","")
        return " where ($us)"
    }

    fun getUpdate(where :String):String{
        var us=""
        mFields.forEach{
           //us += ",${it.key} = ${getValue(it.value!!)} "
            us += ",${it.key} = ${getValue(it.value)} "
        }
        us=us.drop(1)
        return "UPDATE $tableName SET $us $where"
    }

    fun getInsertOrUpdate():String{
        var ms=""
        var vs=""
        var us=""
        mFields.forEach{
            ms += ",${it.key}"
            var value=if(null!=it.value) "${getValue(it.value!!)?:null}" else "null"
            vs += ",$value"
            us += ",${it.key}=$value"
        }
        ms=ms.drop(1)
        vs=vs.drop(1)
        us=us.drop(1)
        return "INSERT INTO $tableName ($ms) VALUES ($vs) ON DUPLICATE KEY UPDATE $us "
    }

    fun getBatchUpdate():String?{
        if(mValues.isEmpty()) return null

        var ms=""
        var vs=""
        var us=""
        mFieldInfos.values.sortedBy { it.order }.forEach{
            ms += ",${it.name}"
            us += ",${it.name}=values(${it.name})"
        }
        ms=ms.drop(1)
        mValues.forEach { vs+=",$it" }
        vs=vs.drop(1)
        us=us.drop(1)
        return "INSERT INTO $tableName ($ms) VALUES $vs ON DUPLICATE KEY UPDATE $us "
    }

    fun valueCount():Int{
        return mValues.size
    }

    fun getInsertIgnore():String{
        var ms=""
        var vs=""
        var us=""
        mFields.forEach{
            ms += ",${it.key}"
            var value=if(null!=it.value) "${getValue(it.value!!)?:null}" else "null"
            vs += ",$value"
            us += ",${it.key}=$value"
        }
        ms=ms.drop(1)
        vs=vs.drop(1)
        us=us.drop(1)
        return "INSERT IGNORE INTO $tableName ($ms) VALUES ($vs)"
    }

    fun getReplace():String{
        var ms=""
        var vs=""
        var us=""
        mFields.forEach{
            ms += ",${it.key}"
            var value=if(null!=it.value) "${getValue(it.value!!)?:null}" else "null"
            vs += ",$value"
            us += ",${it.key}=$value"
        }
        ms=ms.drop(1)
        vs=vs.drop(1)
        us=us.drop(1)
        return "REPLACE INTO $tableName ($ms) VALUES ($vs)"
    }


    fun getDelete():String{
        return "delete from $tableName ${getWhere()}"
    }

    fun getDeleteForever(where :String):String{
        return "delete from $tableName ${where}"
    }

    fun getDelete(fieldName:String,ids:JsonArray):Array<String>{
        var sql=arrayOf<String>()
        ids.forEach {
            var s="delete from $tableName where $fieldName='$it'"
            sql=sql.plus(s)
        }
        return sql
    }

    fun getInSet(key:String):String{
        var s=""
        (mFields[key] as ArrayList<Any>).forEach({s+=","+getValue(it)})
        if((mFields[key] as ArrayList<Any>).size>0){
            s = s.drop(1)
            return "$key in ($s)"
        }else{
            return ""
        }
    }


    /**
     * 从filter json遍历
     */

    fun parseFilters(filters: JsonObject?,keyNames:Map<String,String>?=null):SqlMaker{
        filters?.fieldNames()?.forEach{name->
            var dbFieldName=name
            keyNames?.let{
                dbFieldName=keyNames[name]?:name
            }

            mFields.put(dbFieldName,filters.getValue(name))
        }
        return this
    }


    fun getCountPerDay(beginDate:String,endDate:String,timeFieldName:String="created_time"):String{
        return getCountPer("date","日期",beginDate,endDate,timeFieldName)
    }
    fun getCountPerWeek(beginDate:String,endDate:String,timeFieldName:String="created_time"):String{
        return getCountPer("week","自然周",beginDate,endDate,timeFieldName)
    }
    fun getCountPerMonth(beginDate:String,endDate:String,timeFieldName:String="created_time"):String{
        return getCountPer("month","月份",beginDate,endDate,timeFieldName)
    }
    fun getCountPerYear(beginDate:String,endDate:String,timeFieldName:String="created_time"):String{
        return getCountPer("year","年份",beginDate,endDate,timeFieldName)
    }

    fun getCountPerDayOfWeek(beginDate:String,endDate:String,timeFieldName:String="created_time"):String{
        return getCountPer("dayofweek","时刻",beginDate,endDate,timeFieldName)
    }
    fun getCountPerDayOfMonth(beginDate:String,endDate:String,timeFieldName:String="created_time"):String{
        return getCountPer("dayofmonth","时刻",beginDate,endDate,timeFieldName)
    }

    //必须在数据库中创建工具表nums,并按unit生成2张临时表
    private fun getCountPer(unit:String,caption:String,beginDate:String,endDate:String,timeFieldName:String):String{
        return "DROP VIEW IF EXISTS list_$unit;\n" +
                "\n" +
                "CREATE VIEW list_$unit\n" +
                "AS\n" +
                "SELECT COUNT(*) AS count, $unit($timeFieldName) AS date,datediff(create_time,'$beginDate') as seq " +
                "FROM $tableName\n" +
                "where $timeFieldName>='$beginDate' and $timeFieldName<='$endDate'\n" +
                "GROUP BY $unit($timeFieldName);\n" +
                "\n" +
                "DROP VIEW IF EXISTS date_list_$unit;\n" +
                "\n" +
                "CREATE VIEW date_list_$unit\n" +
                "AS\n" +
                "SELECT 0 AS count, $unit(date) as date,datediff(date,'$beginDate') as seq \n" +
                "FROM (\n" +
                "\tSELECT adddate('$beginDate', numlist.id) AS 'date'\n" +
                "\tFROM (\n" +
                "\t\tSELECT n1.i + n10.i * 10 + n100.i * 100 AS id\n" +
                "\t\tFROM nums n1\n" +
                "\t\t\tCROSS JOIN nums n10\n" +
                "\t\t\tCROSS JOIN nums n100\n" +
                "\t) numlist\n" +
                "\tWHERE adddate('$beginDate', numlist.id) <= '$endDate'\n" +
                ") A;\n" +
                "\n" +
                "select $caption,次数 from(select date as $caption,count as 次数,seq from (select * from date_list_$unit WHERE date not in (select date from list_$unit) union select * from list_$unit) N group by date order by seq asc) O;"

    }

    fun getCountPerHour(beginDate:String,endDate:String,timeFieldName:String="created_time"):String{
        return "DROP VIEW IF EXISTS list_hour;\n" +
                "\n" +
                "CREATE VIEW list_hour\n" +
                "AS\n" +
                "SELECT COUNT(*) AS count, hour(create_time) AS date,datediff(create_time,'$beginDate') as seq FROM $tableName\n" +
                "where create_time>='$beginDate' and create_time<='$endDate'\n" +
                "GROUP BY hour(create_time);\n" +
                "\n" +
                "DROP VIEW IF EXISTS date_list_hour;\n" +
                "\n" +
                "CREATE VIEW date_list_hour\n" +
                "AS\n" +
                "SELECT 0 AS count, hour(date) as date,datediff(date,'$beginDate') as seq \n" +
                "FROM (\n" +
                "\tSELECT adddate('$beginDate', INTERVAL 0 HOUR) AS 'date' union\n" +
                "\tSELECT adddate('$beginDate', INTERVAL 1 HOUR) AS 'date' union\n" +
                "\tSELECT adddate('$beginDate', INTERVAL 2 HOUR) AS 'date' union\n" +
                "\tSELECT adddate('$beginDate', INTERVAL 3 HOUR) AS 'date' union\n" +
                "\tSELECT adddate('$beginDate', INTERVAL 4 HOUR) AS 'date' union\n" +
                "\tSELECT adddate('$beginDate', INTERVAL 5 HOUR) AS 'date' union\n" +
                "\tSELECT adddate('$beginDate', INTERVAL 6 HOUR) AS 'date' union\n" +
                "\tSELECT adddate('$beginDate', INTERVAL 7 HOUR) AS 'date' union\n" +
                "\tSELECT adddate('$beginDate', INTERVAL 8 HOUR) AS 'date' union\n" +
                "\tSELECT adddate('$beginDate', INTERVAL 9 HOUR) AS 'date' union\n" +
                "\tSELECT adddate('$beginDate', INTERVAL 10 HOUR) AS 'date' union\n" +
                "\tSELECT adddate('$beginDate', INTERVAL 11 HOUR) AS 'date' union\n" +
                "\tSELECT adddate('$beginDate', INTERVAL 12 HOUR) AS 'date' union\n" +
                "\tSELECT adddate('$beginDate', INTERVAL 13 HOUR) AS 'date' union\n" +
                "\tSELECT adddate('$beginDate', INTERVAL 14 HOUR) AS 'date' union\n" +
                "\tSELECT adddate('$beginDate', INTERVAL 15 HOUR) AS 'date' union\n" +
                "\tSELECT adddate('$beginDate', INTERVAL 16 HOUR) AS 'date' union\n" +
                "\tSELECT adddate('$beginDate', INTERVAL 17 HOUR) AS 'date' union\n" +
                "\tSELECT adddate('$beginDate', INTERVAL 18 HOUR) AS 'date' union\n" +
                "\tSELECT adddate('$beginDate', INTERVAL 19 HOUR) AS 'date' union\n" +
                "\tSELECT adddate('$beginDate', INTERVAL 20 HOUR) AS 'date' union\n" +
                "\tSELECT adddate('$beginDate', INTERVAL 21 HOUR) AS 'date' union\n" +
                "\tSELECT adddate('$beginDate', INTERVAL 22 HOUR) AS 'date' union\n" +
                "\tSELECT adddate('$beginDate', INTERVAL 23 HOUR) AS 'date' \n" +
                ") A;\n" +
                "\n" +
                "select 时刻,次数 from(select date as 时刻,count as 次数,seq from (select * from date_list_hour WHERE date not in (select date from list_hour) union select * from list_hour) N group by date order by date asc) O;"
    }
}

public fun String.toArray(decimal:String?=null):Array<String>{
    decimal?.let {
        var list = arrayOf<String>()
        this.split(decimal).forEach {
            if(!it.isEmpty())
                list = list.plus(it)
        }
        return list
    }

    return arrayOf(this)
}