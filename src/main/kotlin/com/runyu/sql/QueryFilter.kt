package com.runyu.sql

import com.runyu.std.Const.Prompt.VAL_BAD_FILTER
import com.runyu.std.Msg
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject

class QueryFilter{
    class Rule(var fileldName:String,var op:String,var value:Any? )
    protected var mRules=HashMap<String, Rule>()


    fun add(fileldName:String,op:String,value:Any?):QueryFilter{
        mRules.put(fileldName,Rule(fileldName,op,value))
        return this
    }


    fun build(ingoreFilelds:Set<String> =setOf()):String{
        if(0==mRules.size) return ""

        var filters=""

        mRules.values.forEach {
            if(!ingoreFilelds.contains(it.fileldName)) {
                if("="==it.op&&"null"==getValue(it))//针对空的情况改为isnull函数
                    filters += " AND isnull(${it.fileldName}) "
                else
                    filters += " AND ${it.fileldName} ${it.op} ${getValue(it)} "
            }else
                filters += " AND (${it.fileldName} ${it.op} ${getValue(it)} OR isnull(${it.fileldName}))  "//允许空值
        }
        return " where ( ${filters.drop(4)})"
    }

    fun get():JsonArray{
        if(0==mRules.size) return JsonArray()

        var filters=JsonArray()

        mRules.values.forEach {
            filters.add(Msg().add("fn",it.fileldName).add("op",it.op).add("fv",it.value))
        }
        return filters
    }

    private fun getValue(rule:Rule):String{
        if(null==rule.value)
            return "null"
        else if(rule.op=="in") {
            return "${rule.value}"
        }else if(rule.op=="between"){
            return "${rule.value}"
        }else if(rule.value is String)
            return "'${rule.value}'"

        return rule.value.toString()
    }



    companion object{
        fun parse(filters: JsonArray?, keyNames:Map<String,String>?=null):QueryFilter{
            var qf = QueryFilter()
            try {
                filters?.forEach { item ->
                    item as JsonObject
                    var j = Msg(item)
                    var name = j.get<String>("name",j.get("fn"))
                    var dbFieldName = name
                    //尝试替换key的名字
                    keyNames?.let {
                        dbFieldName = keyNames[name] ?: name
                    }

                    //取操作符合数据
                    var op = j.getString("op", "=").trim().toLowerCase()
                    var v = j.getValue("v",j.getValue("fv",null))

                    if("in"==op)
                        qf.add(dbFieldName,op,getValuesOfIn(v as String))
                    else if("null"==op){
                        parseNull(qf,dbFieldName,v as String)
                    }else if("between"==op){
                        var v2 = j.getValue("v2",j.getValue("fv2",null))
                        if(null!=v && null!=v2) {//如果有一个是空，则放弃
                            if (v2 is String)
                                qf.add(dbFieldName, op, "'$v' and '$v2'")
                            else
                                qf.add(dbFieldName, op, "$v and $v2")
                        }
                    }else {
                        //如果是字符串+like，将格式转化下
                        if (v is String) {
                            if (!v.isNullOrEmpty()) {
                                if ("like" == op)
                                    qf.add(dbFieldName, op, "%$v%")
                                else
                                    qf.add(dbFieldName, op, v)
                            }
                        } else if (null != v)
                            qf.add(dbFieldName, op, v)
                    }
                }
            }catch (e:Exception){
                throw ExecptionSqlFilter()
            }
            return qf
        }

        fun getValuesOfIn(value:String?):String{
            value?.let{
                var ret="("
                var list=value.split(",")
                list.forEach {
                    try{
                        var num=it.toInt()
                        ret=ret+ num +","
                    }catch (e:Exception){
                        ret=ret+"'$it',"
                    }
                }
                ret=ret.dropLast(1)+")"
                return ret
            }
            return "null"
        }

        fun parseNull(qf:QueryFilter,fieldName:String,value:String?){
            value?.let{
                when(value.trim().toLowerCase()){
                    "null"-> qf.add(fieldName,"=",null)//等待后面解析替换
                    "notnull"->qf.add(fieldName,"is not",null)
                    else -> null
                }
            }
        }
    }
}

class ExecptionSqlFilter:Exception(VAL_BAD_FILTER)