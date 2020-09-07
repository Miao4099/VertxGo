package com.runyu

import com.runyu.app.*
import com.runyu.com.runyu.worker.WorkerMaster
import com.runyu.com.runyu.worker.WorkerMaster.Vertx
import com.runyu.com.runyu.worker.WorkerMaster.getCfg
import com.runyu.common.TimeCenter
import com.runyu.map.Cache
import com.runyu.std.Msg
import com.runyu.std.VertMsg
import com.runyu.user.CusUser

import com.runyu.worker.WorkSupport


fun main(args: Array<String>) {
    println("starting....")


    WorkerMaster.initSystem("2000") { v, cfg->
        //初始化Redis
        CusUser.init(getCfg("redis"))

        Cache.init(getCfg("cache"))


        //初始化Sql
        var sql=AppMySql().setup(getCfg("worker_sql"))
        WorkerMaster.setWorkerSqlName("worker_sql")

        //初始化Agent
        AppHttpAgent().setup(getCfg("worker_agent"))


        TimeCenter.minuteChange { old, new->
            VertMsg().post("sql", Msg().msgId("MSG_TEST_TEST"))

        }.dayChange { old, new->
            VertMsg().post("config", Msg().msgId("MSG_SAVE_VISIT_COUNT_DAY"))
        }
            .start()

        //应该有个后台统计任务，启动时自动查询blog等数据进行缓存，或者执行时自动保存
    }
}