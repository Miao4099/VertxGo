这个框架是用于快速开发Web App，基于Kotlin编写，基于异步的Vertx，建立了一种应用开发模式，已将很多Vertx的细节隐藏起来，让使用者可以专注于自己的实际内容开发，而不用学习冗长的基本知识。是一个高度定制化的、思路直接的一个框架。特点总结如下：
1.生成的最终文件是jar，包含客户应用与web server，java -xxx.jar 即可以全套运行，管理方便；
2.使用Gradle作为包管理，使用时可以只导入一个jar包就可以使用全部功能；
3.99%的http请求统一封装为post方式，使用输入和输出都是json，客户开发集中于处理一个输入的json，并输出一个处理过的json；
4.内部模块间数据交换和配置全部使用json；
5.全异步处理；
6.提供了关于json,validator、sql查询，redis，Rabittmq，微信message等二次包装的大量工具类；
7.特别适合关注于完成手头任务而不想知道细节的人群;
8.最大可能的使用了连续呼叫的方式来定义接口；
9.使用配套的基于VUE-element-admin的前端框架，可以非常迅速的开发对应的管理前端，如在一天内完成多张数据库表的增删改查，对应配套的排序和过滤功能。

对使用者的要求：
1.理解异步概念
2.理解sql语句
3.理解json

一、快速上手：
1.定义main
    //读取配置1000.json启动
    WorkerMaster.initSystem("1000") { v, cfg->
        //从1000.json文件中读取 worker_sql部分来初始化Sql worker
        var sql=AppMySql().setup(getCfg("worker_sql"))

        //从1000.json文件中读取 worker_agent部分来初始化Http server worker
        AppHttpAgent().setup(getCfg("worker_agent"))

    }

2.  定义AppHttpAgent
    override fun addHandler(router: Router) {
    //定义/admin/slaver/*和/slaver/* 两个路径,收到的消息打包后转发到名字叫sql的worker
	// /admin/slaver/add和/slaver/add 都会转化为MSG_SLAVER_ADD发出到名字叫sql的worker
	   RestfulAny("/admin","/slaver",router,this,"sql")
    }

3.  定义AppMySql
    override fun start(config: String): WorkShop {
        super.start(config)
	//从传入的config字符串中读取配置，初始化JDBC客户端
        mClient = JDBCClient.createNonShared(Vertx(),AnyJson(config))

	//注册消息处理：指定msgUserUpdate接口处理MSG_USER_UPDATE消息
        Dispatcher()
                .add("MSG_USER_UPDATE", this::msgUserUpdate)
    }
4.  AppMySql中定义消息处理
fun AppMySql.msgUserUpdate(key: String, msg: JsonObject, message: Message<JsonObject>):Msg {
    //key 是消息ID，如MSG_USER_UPDATE
    //msg 是传入的需要处理的数据消息，格式是json
    //message，需要返回一个json作为处理结果，大量的工具类已经进行了包装，只需要传入message到工具类就行
    return tryDo(message) {
    //读取msg中传入的各种参数并进行校验后形成sql语句，在参数传入的同时进行校验，校验失败的话直接返回各种校验异常结果，这些异常由tryDo统一处理。
    //不想进行校验的话，可以忽略set中的最后一个参数


        var sql = sqlUser.sql()
                .set<String>("user_id", msg, ValId())
                .set<String>("user_avatar", msg,ValImage("头像",true))
                .set<String>("user_name", msg, ValName())
                .set<String>("user_role", msg, ValRole())
                .set<String>("memo", msg, ValMemo())
                .set<Int>("user_sex", msg,ValSex(true))
                .set("user_password", IDGen.md5(password!!))
                .getUpdate("where user_id='${msg.jsonGet<String>("user_id")}'")

	//业务处理，传入message
        transaction(sql.toArray(), message)
    }
}

5.对应的配置文件1000.json
{
  "host": "127.0.0.1",
  "group_name": "com.runyu.blog",
  "cfg_log": "config/log/log4j2.xml",

  "worker_sql": {
    "work_id": "sql",
    "max_pool_size": 30,
    "user": "root",
    "password": "123456",
    "url": "jdbc:mysql://127.0.0.1:3306/blog_pro?serverTimezone=GMT%2B8&characterEncoding=utf8"
  },

  "worker_agent": {
    "work_id": "agent",
    "ssl_on": true,
    "jks": "config/api.xxx.jks",
    "jks_password": "1161",
    "port": 81
  }

}


二、快速启动
   1.打开工程后，首先编辑1000.json中worker_sql中的url字段，这个用于连接数据库
   2.将config目录（包含子目录）都复制到运行目录，如D:/
   3.运行Gradle中的 Tasks-build-jar 配置生成jar文件，如test.jar
   4.将生成的jar复制到运行目录
   5.执行java -jar test.jar,如果数据库连接OK，会自动生成相关的表
   6.如果在本机运行，可以访问 http://localhost，就可以访问配置在admin目录下的后台管理前端,当然也可以使用postman来访问接口

三、高级功能
    1.数据库表的映射方式。在使用中，数据库表中的字段名会有一种命名，各种原因下，前端经常会使用另外一种命名，经常给后端开发增加痛苦。为了将这种命名映射无痛苦的完成，可以简单地
    2.将后台的分页等功能与标准查询做了分层隔离，使用from就可以直接将一个复杂的sql语句直接作为输入来进行查询，使用户从分页这种细节中解脱出来
    3.自定义Validator
      worker在处理消息时需要截取大量的字段信息，从httpAgent传入的参数都是包装在json字段中，截取时需要校验是否合法，validator就是做这个用的。
    4.VertMsg用于模块间的通信
    5.系统定时任务
      系统中经常会用到一些定时器，用于定时保存一些数据、心跳、或者做一些其它事情。特地封装了TimeCenter用于这些目的，用户只需注册对应的handler就可以在定时满足要求时收到请求，已经支持的handler有：
      A.每分钟
      B.每刻钟
      C.每小时
      D.每天
    6.Restful和各种继承类的使用
    7.微信小程序和公众号的支持
    8.Redis和Cache
    9.动态生成并输出html
    10.log的配置
    11.用户登录的固化
    12.worker的概念和常用的block方法
       worker将Vertx中的Verticle进行了包装，将初始化和启动标准化和简单化了,每个work都是处理接收到json,处理后再返回一个json。其中 A.用户需要重载setup()方法，并在setup方法里解析和处理对应的配置字段，和注册消息处理接口 B.实现对应的消息处理函数。Vertx中处理耗时任务时必须要特殊处理，也做了对应的包装。

    13.Vue-element-admin前端和定制菜单

