package com.runyu.app

import com.runyu.common.oss.OssClient
import com.runyu.worker.WorkShop
import com.runyu.worker.WorkSql
import io.vertx.core.json.JsonObject


class AppMySql: WorkSql() {
    var mOssClient:OssClient?=null
    override fun start(config: String): WorkShop {
        super.start(config)

        Dispatcher()
                .add("MSG_NEWS_ADD", this::msgNewsAdd)
                .add("MSG_NEWS_DEL", this::msgNewsDel)
                .add("MSG_NEWS_UPDATE", this::msgNewsUpdate)
                .add("MSG_NEWS_GET", this::msgNewsGet)
                .add("MSG_NEWS_LIST",this::msgNewsList)


        return this;
    }



    fun Product(id:String): JsonObject{
        Map("products")?.get(id)?.let{
            return it as JsonObject
        }
        return JsonObject()
    }
}



