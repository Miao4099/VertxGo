package com.runyu.user

import com.runyu.com.runyu.worker.WorkerMaster
import com.runyu.map.RedisDualMap
import com.runyu.sql.QueryResult
import com.runyu.sql.SqlMaker
import com.runyu.std.*
import com.runyu.std.Const.MsgBody.MSG_DB_CMD
import com.runyu.std.Const.Prompt.USER_FOUND
import io.vertx.core.json.JsonArray


import io.vertx.core.json.JsonObject

import java.time.LocalDateTime

class User{
    var  user_id:String=""
    var  expiredTime=""
    var  token=""
    var  wxToken=""
    var  name=""
    var  role=""
    var  sex=1
    var  type=1
    var  avatar:String?=""
    var  country:String?=""
    var  province:String?=""
    var  city:String?=""
    var  openid:String?=""
    var  user_state:Int=0
    var  extra:HashMap<String,Any>?=null

    fun  wxToken(token:String):User{
        wxToken=token
        return this
    }

    fun  token(token:String):User{
        this.token=token
        return this
    }


    fun toJson():AnyJson{
        return AnyJson.toJson(this)
                .del("extra")
                .del("expiredTime")
                .add("role", JsonArray().add(role))

    }
}


object CusUser{
    /**
     * key1是普通账号id，key2是token
     */
    private var mUsers:RedisDualMap<User>?=null

    public  var ALLOW_MULTI_LOGIN=true

    fun init(cfg:String){
        mUsers= RedisDualMap<User>(cfg,User().javaClass)
    }

    fun format(s:String):String{
        return s
        //return s.replace("T"," ").substring(0,19)
    }

    private fun handleSameUserLogin(userId:String?){
        userId?.let {
            if (!ALLOW_MULTI_LOGIN) {
                mUsers?.clear(userId)
            }
        }
    }

    /**
     * 通過token直接拿到user信息，如果沒有返回null
      */
    fun getUser(token:String?):User?{
        if(null==token) return null

        try {
            mUsers?.getByK2(token)?.let {
                if (LocalDateTime.parse(it.expiredTime)!!.compareTo(LocalDateTime.now()) < 0) {
                    mUsers?.clear(it.token)
                    return null
                } else {
                    return it
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
            Log.e("getUser","fail to get user by token=$token")
        }
        return null
    }


    /**
     * 通過token直接拿到user，退出登录
     */
    fun logout(token:String):Boolean{
        mUsers?.getByK2(token)?.let{
                mUsers?.clear(it.token)
                return true

        }
        return false
    }

    /**
     * 根据用戶的openid直接創建新用戶，因为这些用户没有密码
     */
    fun addUser(user:User){
        mUsers?.put(user.user_id, user.token, user)
        var s=SqlMaker("users")

    }

    /**
     * 用戶登錄,每次都查询数据库，如果Redis没有，则创建
     */
    fun loginUser(username:String, password:String, block:(user:User?,err:SysErr)->Unit, parse:(user:User, json:JsonObject)->(HashMap<String,Any>)?) {
        WorkerMaster.getWorkerSqlName().let { sqlName ->
            var pw = IDGen.md5(password)
            var msg=Msg().msgId(Const.MsgID.MSG_DB_SINGLE_QUERY).add(MSG_DB_CMD,"select * from users where user_name='$username' and user_password='$pw'")
            VertMsg().post(sqlName,msg,{rsp->

                var numRows = QueryResult(rsp).numOfRow()
                if (0 == numRows) {

                    block.invoke(null, Const.ErrUserNotFound())

                } else if (1 == numRows) {
                    var user=QueryResult(rsp).one<UserAdaptor>()

                    handleSameUserLogin(user?.user_id)

                    //開始創建一個新用戶
                    var token = IDGen.md5(IDGen.strId())
                    var u = User()
                    u.token = token
                    user?.let { user ->
                        u.name = user.user_name
                        u.avatar = user.user_avatar
                        u.user_id = user.user_id
                        u.role = user.user_role
                        u.sex = user.user_sex
                        u.type = user.user_type
                        u.country = user.user_country
                        u.province = user.user_province
                        u.city = user.user_city

                    }

                    u.expiredTime =
                        format(LocalDateTime.now().plusSeconds(WorkerMaster.getUserTokenTimeout()).toString())//自動延長時間2個小時
                    //保存特殊字段的信息到user
                    u.extra=null//释放老的数据
                    u.extra = parse(u, rsp.getJsonArray("rows").getJsonObject(0))
                    //以用户自定义流程传回的userId作为key

                    //状态不为3的才是正常用户
                    if(3 != user?.user_state) {
                        mUsers?.put(u.user_id, token, u)
                        block.invoke(u, SysErr(0, USER_FOUND))
                    }else{
                        block.invoke(u, Const.ErrUserDisabled())
                    }
                } else {
                    block.invoke(null, Const.ErrUserDuplicated())
                }
            }, {rsp->
                block.invoke(null, Const.BadResponse())
            })//10s登录时间
        }
    }



}