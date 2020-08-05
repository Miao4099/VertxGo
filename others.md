
# 其它扩展功能
## 3.怎样读取输入json中的数据
        var sql = sqlUser.sql()
                .set<String>("user_id", msg, ValId())
                .set<String>("user_avatar", msg,ValImage("头像",true))
                .set<String>("user_name", msg, ValName())
                .set<String>("user_role", msg, ValRole())
                .set<String>("memo", msg, ValMemo())
                .set<Int>("user_sex", msg,ValSex(true))
                .set("user_password", IDGen.md5(password!!))
                .getUpdate("where user_id='${msg.jsonGet<String>("user_id")}'")

set里包装了从json中获取数据的各种方式。set<xxx>,xxx是指要读取的参数类型，如String,Int，Boolean等；“user_id”,"user_avatar"等是指数据表的字段名称，msg是标准的输入参数
        

## 4.直接操作一张数据表和字段名称映射

一般来说，前端和后端的使用的字段名称是不太一样的，有的是因为设计原因、有的是故意保密。无论何种原因，后端对做这种转换是深恶痛绝的，而且会被前端的代码所影响要反复修改，前端也是面临类似的问题。

        var sql = sqlUser.sql()
                .set<String>("user_id", msg, ValId())
                .set<String>("user_avatar", msg,ValImage("头像",true))
                .set<String>("user_name", msg, ValName())
                .set<String>("user_role", msg, ValRole())
                .set<String>("memo", msg, ValMemo())
                .set<Int>("user_sex", msg,ValSex(true))
                .set("user_password", IDGen.md5(password!!))
                .getUpdate("where user_id='${msg.jsonGet<String>("user_id")}'")

上面代码中使用的**sqlUser**是放置字段名称映射的工具类的实例，其中包含2个字段的映射关系：score：index_of_score, city:user_citry, index_of_score和user_city是sql表中的字段名称，用户可以添加类似的内容。users是数据表的名称。

        var sqlUser=SqlBuilder("users", mapOf(
                Pair("score","index_of_score"),
                Pair("city","user_city"))
        )
        
## 5.稍微复杂些的分页、过滤、排序操作
上面的第4个问题，是操作一张简单的表。如果比较复杂的数据，比如是多个数据表join查出的数据，还需要进行分页、过滤 、排序处理，也可以简单的结合使用from接口，并使用标准的过滤器、排序器、分页器解析。

        fun AppMySql.msgMachineList(key: String, msg: JsonObject, message: Message<JsonObject>): Msg {
            return tryDo(message) {
                //获得标准的分页请求参数 
                var pi=msg.pageInfo()

                //解析json获取排序参数，输出结果默认按create_time字段进行降序排序
                var qs=sqlMachine.qs(msg, QuerySorter.Rule("create_time", "desc"))

                //from是将一个复杂的sql语句中直接作为一张表来处理
                var sql = sqlMachine.sql()
                        .from("SELECT H.*,K.NAME as shop_name,K.contact,K.phone AS contact_phone,K.slogan FROM (\n" +
                                "SELECT machine_number,`status`,remark,province,city,district,address,D.*FROM (\n" +
                                "SELECT A.machine_id,machine_number,province,city,district,address,B.`status`,B.remark FROM                                             machine_location A RIGHT JOIN machines B ON A.machine_id=B.id) C LEFT JOIN machine_setting D ON                                 C.machine_id=D.machine_id GROUP BY machine_number) H left JOIN store K ON                                                               K.machine_id=H.machine_id ")
                        .getList(sqlMachine.fs(msg), qs, pi)//解析json获取过滤参数后，与qs，pi一起输入生成产生list的sql语句

                //查询出列表
                listQuery(sql, message,{json->
                    //分析输出的结果，结果是json，里面包含数据的array    
                    var qr=QueryResult(json)
                    //每一行输出都做部分处理
                    qr.loopRows { addNewsField(it) }
                })
            }
        }


上面代码中使用的**sqlMachine**是放置字段名称映射的工具类的实例，用户还是可以添加字段映射的内容。

## 6.怎样添加一个自定义shop
Shop是对Vertx中Verticle的封装，可以让其使用标准的json配置。在程序中，可以创建一个或多个Agent shop接收不同端口的请求，然后将其打包为统一格式的json发给后方的Task Shop，task shop就是完成你的任务的shop，可以多个。具体框架参考下图  
        ![image](/images/framework.png)   
更详细的步骤可以参考 “快速上手” 部分。每个新的shop继承WorkShop，重载setup接口，config是传入的json配置参数
    
            override fun setup(config:String,instanceCount:Int): WorkShop {
                super.start(config)
                //创建JDBC client        
                mClient = JDBCClient.createNonShared(Vertx(),AnyJson(config))

                //注册消息处理接口，如MSG_USER_ADD消息由msgUserAdd接口处理        
                Dispatcher()
                        .add("MSG_USER_ADD", this::msgUserAdd)
            }
    
    
## 7.怎样给一个Shop发送自定义消息
在VertxApp中，http server收到的消息直接被ResfulXXX打包发动到指定的shop，如果用户自己需要定制消息发送的话参见下面的例子，其中mLoginStateReceiver是接收消息的shop的名字，第二个参数是用户按照格式要求打包的json，json中有个“json”字段存放需要shop处理的数据和参数：  

        VertMsg(Vertx()).post(mLoginStateReceiver, Msg().msgId("MSG_USER_LOGIN_SUCCESS").json(Msg().add("user_id", user!!.user_id)))
        
如果需要发送消息后处理返回的结果，参照下面的例子：  

            VertMsg(Vertx()).post("message",//接收的shop名字叫做message
                Msg().msgId("MSG_MESSAGE_ADD") //发送的数据包
                    .json(
                        Msg().add("message_id", msgId)
                            .add("message_body", msgBody)
                            .add("send_count", resendCount)
                            .add("valid_time", validTime)
                    ), { rsp ->
                    block?.invoke(true, rsp) //这里可以处理返回的rsp，json格式
                }, { rsp ->
                    block?.invoke(false, if (rsp.isNullOrEmpty()) "{}" else rsp) //发送失败的时候返回的rsp
                })
                
                
## 8.怎样使用RestfulXXX并管理访问权限
数据中users (名字是固定的)表中的role字段定义了用户分组，前端用户的菜单、和各个接口都可以根据role来调整访问权限。使用RestfulPost、RestfulAny传入接口要求的role，RestfulPost、RestfulAny会判断比较用户的权限和这个传入的role，如果符合要求才会将请求发给模块，否则直接返回权限不足的消息。用户需要在派生HttpAgent时，重载    override fun addHandler(router: Router) {} 添加你本人需要添加的http request router，一个请求对应一个路径。如：  
            //定义编辑者权限:用户的role是admin或者editor就满足   
            
            var editorAccess=setOf("admin","editor")
            
            
### A.RestfulPost  
            //定义接收post方法的路径，默认有list/get/add/del/update 5种，最后一个参数添加了4个,也就是说
            //访问路径是http://xxx/ps/vip/add(del\get\list\update\login\logout\password\update_product)9个，同时也是
            //http://xxx/vip/add(del\get\list\update\login\logout\password\update_product)9个
            //需要满足editor权限 
            RestfulPost("/ps","/vip",router,this,"sql",editorAccess,setOf("login","logout","password","update_product"))
            
### B.RestfulAny  
            //路径是/ps/chart 或者/chart的满足editor权限的所有请求
            RestfulAny("/ps","/chart",router,this,"sql",editorAccess)
            
### C.RestfulEasy
            //不检查任何权限的请求
            RestfulEasy("/ps","/chart",router,this)

## 10.怎样定义静态网页网站
一般的后台开发需要开放一个静态网页的作为后台，如基于VUE的后台（vue-element-admin）,可以用一句代码设置一个服务器上的静态目录为静态网页服务器、

    override fun addHandler(router: Router) {
        ...
    
        setStdStaticHtml("/ad","ad")
    }


## 11.怎样定义BrowserHistory
有些前端，如UniApp,需要对某些访问路径做特殊的返回，可以使用如下方法：  

        override fun addHandler(router: Router) {
                ...
                
                setBrowserHistoryPath(setOf("/shengChan","/info","/newdetail","/rawArticle"),"/index.html")
        }

        
## 12.怎样使用统一定时器
在程序中经常需要定时器来完成一些任务，比如心跳、定时保存、定时任务等，开太多的定时器比较难管理代码维护也麻烦，此时可以采用TimeCenter来完成任务，里面只开了一个定时器，包括但是在每分钟、每刻钟、每小时、每天都会产生对应的定时事件可以使用。

        TimeCenter.minuteChange { old, new->
            VertMsg(Vertx()).post("config", Msg().msgId("MSG_SAVE_MILESTONE_VISIT_COUNT"))
        }.quarterChange { old, new,i->
            //15分钟发送一次
            VertMsg(Vertx()).post("message", Msg().msgId("MSG_MESSAGE_SEND"))
        }.dayChange { old, new->
            //一天保存一次，用于后期统计
        //    VertMsg(Vertx()).post("config", Msg().msgId("MSG_SAVE_VISIT_COUNT_DAY"))
        }.start()
