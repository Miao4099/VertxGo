package com.runyu.sql

import com.runyu.sql.QueryFilter
import com.runyu.sql.SqlMaker
import com.runyu.std.Msg
import com.runyu.std.PageInfo
import com.runyu.std.pageInfo
import io.vertx.core.json.JsonObject

class SqlBuilder(var tableName:String,var keyNames:Map<String,String> = mapOf()) {
    fun fs(msg:JsonObject):QueryFilter{
        return QueryFilter.parse(Msg(msg).filters(), keyNames)
    }

    fun qf(msg:JsonObject):QueryFilter{
        return QueryFilter.parse(Msg(msg).filters(), keyNames)
    }

    fun qs(msg:JsonObject, default: QuerySorter.Rule?=null):QuerySorter{
        return QuerySorter.parse(Msg(msg).sorter(),default, keyNames)
    }

    fun pi(msg:JsonObject):PageInfo{
        return msg.pageInfo()
    }

    fun sql()=SqlMaker(tableName).setAlias(keyNames)

}