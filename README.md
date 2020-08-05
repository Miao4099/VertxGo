# VertxApp
## 简介  
   VertxApp是Vertx的Kotlin包装，使用简单、扩展方便。这个框架是用于快速开发Web App，基于Kotlin编写，基于异步的Vertx，建立了一种应用开发模式，已将很多Vertx的细节隐藏起来，让使用者可以专注于自己的实际内容开发，而不用学习冗长的基本知识。是一个高度定制化的、适合思路清晰的开发者的一个框架。特点总结如下：  
1.生成的最终文件是jar，包含客户应用与web server，java -jar xxx.jar 即可以全套运行，管理方便;  
2.使用Gradle作为包管理，使用时可以只导入一个jar包就行  
3.99%的http请求统一封装为post方式，使用输入和输出都是json，即让客户开发集中于“处理一个输入的json，并输出一个处理过的json”  
4.内部模块间数据交换和配置全部使用json  
5.全异步处理  
6.提供了关于json、validator、sql，redis，微信message等二次包装后的大量工具类  

## 对使用者的要求  
1.理解异步概念  
2.理解sql语句  
3.理解json  

## 快速上手  
1.  定义main
    //读取配置1000.json启动  
	
	    WorkerMaster.initSystem("1000") { v, cfg->  
		   //初始化Sql worker  
		   AppMySql().setup(getCfg("worker_sql"))  

		   //初始化Http server worker
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
		    //初始化JDBC客户端
		mClient = JDBCClient.createNonShared(Vertx(),AnyJson(config))

		    //注册消息处理
		Dispatcher()
			.add("MSG_USER_UPDATE", this::msgUserUpdate)
	    }

4.  AppMySql中定义消息处理

		fun AppMySql.msgUserUpdate(key: String, msg: JsonObject, message: Message<JsonObject>):Msg {

			return tryDo(message) {

				//读取msg中传入的各种参数并进行校验后形成sql语句，校验失败的话直接返回各种校验异常结果
				var sql = sqlUser.sql()
						.set<String>("user_id", msg, ValId())
						.set<String>("user_avatar", msg,ValImage("头像",true))
						.set<String>("user_name", msg, ValName())
						.set<String>("user_role", msg, ValRole())
						.set<String>("memo", msg, ValMemo())
						.set<Int>("user_sex", msg,ValSex(true))
						.set("user_password", IDGen.md5(password!!))
						.getUpdate("where user_id='${msg.jsonGet<String>("user_id")}'")

				//业务处理
				transaction(sql.toArray(), message)
			}
		}

5.  对应的配置文件1000.json

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


## 扩展功能
### 1.生成sql语句来操作一张数据表
SqlBuilder这个类用于简化生成sql语句的工作，用户只需填入响应的字段名称，再将收到的msg传入即可，如果需要校验的话可以传入针对这个字段的校验类。       

        var sql = sqlUser.sql()
                .set<String>("user_id", msg, ValId())
                .set<String>("user_avatar", msg,ValImage("头像",true))
                .set<String>("user_name", msg, ValName())
                .set<String>("user_role", msg, ValRole())
                .set<String>("memo", msg, ValMemo())
                .set<Int>("user_sex", msg,ValSex(true))
                .set("user_password", IDGen.md5(password!!))
                .getUpdate("where user_id='${msg.jsonGet<String>("user_id")}'")

上面代码中使用的**sqlUser**是SqlBuilder实例，users是数据表的名称。最终生成的是update语句

### 2.怎样增加字段校验功能  
  tryDo用于捕获Validator产生的异常，并输出Validator校验失败的具体原因。用户只需要根据派生Validator,自定义Valiator类并传入set接口就行，如：
  
		open class ValSlaverKind() : Validator() {
		    override fun validate(value: Any?):Any? {
			if (null == value)
			    throw ValException("评论种类不能为空")

			if (!setOf("product","raw").contains(value.toString().toLowerCase()))
			    throw ValException("评论种类不合法")

			return value
		    }
		 }
	  
	  //定义后传入set  
    	 .set<String>("slaver_kind", msg, ValSlaverKind())
其中：  
a)判断非空和字段只有是product和raw其中一个才满足要求  
b)return返回的是合法的数据  
c)可在return时返回处理过的数据，如将原始的整型数据改为字符串类型  

### 其它扩展功能地址[others.md](https://github.com/Miao4099/VertxApp/blob/master/others.md "others.md")

## 代码的模板   
### 模板一 [sample_01](https://github.com/Miao4099/VertxApp/blob/master/sample_01 "sample_01")  
代码中完成了get/list/add/del/update 5种标准方法  

## 引用声明
为了使用方便，库中已经直接加入和引用了第三方的源代码，有清楚作者者请通知我修改如下声明：  
1.IDGen，已查不到第三方的信息，从网上直接引用java代码并做了转换  
2.IDCard，已查不到第三方的信息，从网上直接引用java代码并做了转换  
3.PKCS7Encoder，参考腾讯的代码做了改动  
