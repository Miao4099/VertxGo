package com.runyu.sql

import com.runyu.sql.ExecptionSqlFilter
import com.runyu.sql.QueryFilter
import com.runyu.std.AnyJson
import com.runyu.std.Const
import com.runyu.std.Msg
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject

class QuerySorter() {
    //排序有优先级
    class Rule(var fileldName:String,var order:String,var seq:Int=0 )
    protected var mRules=HashMap<String, Rule>()

    fun add(fileldName:String,sort:String):QuerySorter{
        mRules.put(fileldName, QuerySorter.Rule(fileldName, sort,mRules.size))
        return this
    }


    fun build():String{
      //  if(0==mRules.size) return "order by $defFieldName desc"

        if(0==mRules.size) return "   "

        var sorters=""

        mRules.values.sortedBy {it.seq }.forEach {
            sorters+=",   ${it.fileldName} ${it.order} "
        }
        return " order by  ${sorters.drop(4)}"
    }

    fun get():JsonArray{
        if(0==mRules.size) return JsonArray()

        var filters=JsonArray()

        mRules.values.forEach {
            filters.add(Msg().add("fn",it.fileldName).add("fv",it.order))
        }
        return filters
    }

    companion object{
        fun parse(sorters: JsonArray?, default:Rule?=null,keyNames:Map<String,String>?=null): QuerySorter {
            var qs = QuerySorter()
            try {
                sorters?.forEach { item ->
                    item as JsonObject
                    var j = Msg(item)
                    var name = j.get<String>("name", j.get("fn"))
                    var dbFieldName = name
                    //尝试替换key的名字
                    keyNames?.let {
                        dbFieldName = keyNames[name] ?: name
                    }

                    var v = j.getString("order", null)

                    if (!dbFieldName.isNullOrEmpty()&&!v.isNullOrEmpty() && ("+" == v || "-" == v)) {
                        qs.add(dbFieldName, if("+"==v) "ASC" else "DESC")
                    }
                }

                //加入默认值
                if(null!=default&&0==qs.mRules.size)
                    qs.add(default.fileldName,default.order)

            }catch (e:Exception){
                throw ExecptionSqlSorter()
            }
            return qs

        }
    }
}

class ExecptionSqlSorter:Exception(Const.Prompt.VAL_BAD_FILTER)