package com.runyu.sql

class QueryMaker(var sql:String,var countSql:String=""){
    var mFilters=arrayOf<String>()
    var mLimit=""
    var mOrderBy=""

    fun addLimit(pageIndex:Int, pageSize:Int):QueryMaker{
        if(pageIndex!=0&&pageSize!=0) {
            var startIdx = (pageIndex - 1) * pageSize
            mLimit = " limit $startIdx,$pageSize"
        }
        return this
    }

    fun orderBy(orderBy:String):QueryMaker{
        mOrderBy=orderBy
        return this
    }


    fun filerByStatus(status:Int):QueryMaker{
        if(0==status||1==status)
            mFilters = mFilters.plus("(status = $status)")
        return this
    }


    fun filerByString(key:String,keyword:String?):QueryMaker{
        if(keyword.isNullOrEmpty())
            return this
        else {
            mFilters = mFilters.plus("($key like '%$keyword%')")
            return this
        }
    }

    fun filerBySql(sql:String):QueryMaker{
        mFilters=mFilters.plus( sql)
        return this
    }

    fun get():String{
        var filter=" "
        mFilters.forEach { filter+="and "+it }
        filter.drop(3)
        return sql+filter+" "+mOrderBy+" "+mLimit
    }

    fun getCount():String{
        var filter=" "
        mFilters.forEach { filter+="and "+it }
        filter.drop(3)
        return countSql+filter
    }
}