package com.runyu.http

import com.runyu.com.runyu.worker.WorkerMaster
import com.runyu.std.Const
import com.runyu.std.Const.Prompt.USER_FOUND
import com.runyu.std.Const.Prompt.USER_NOT_LOGIN
import com.runyu.std.IDGen
import com.runyu.std.Log
import com.runyu.std.Msg
import com.runyu.user.CusUser
import com.runyu.user.User
import com.runyu.wechat.PKCS7Encoder.decode
import com.runyu.wechat.WxService
import com.runyu.worker.WorkHttpAgent
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.RoutingContext
import sun.misc.BASE64Decoder
import java.nio.charset.Charset
import java.time.LocalDateTime
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class WxHandler(var appKey:String, secret:String,var path:String, var router: Router, var agent: WorkHttpAgent, var dbWorkId:String) {
   // private val mWxService= WxService("wxa0711570188d81fe", "94e128e89af98c1bdd8f5598dd14c182")
   // private val mWxService= WxService("wx2de316d950a49f42","c6783fc448936f97b2c1a99bbb41b884")//山青花燃
   // private val mWxService= WxService("wxebef4e06df58476a", "2383cac88c6c98e74566ca910d455178")//askq小程序
    private val mWxService= WxService(appKey,secret)//山青花燃
    private var mHanlder:((user:User,userInfo:JsonObject)->Unit)?=null
    init{
        router.POST("$path/wxLogin",this::handleWxLogin)
        router.POST("$path/wxGetUser",this::handleWxGetUser)
        router.POST("$path/wxUserInfo",this::handleWxInfoUser)


        router.post()
    }

    fun wxService():WxService{
        return mWxService
    }

    fun setHandler(block:(user: User, userInfo:JsonObject)->Unit):WxHandler{
        mHanlder=block
        return this
    }

    private fun handleWxLogin(rc: RoutingContext):Unit{
        val code = rc.request().getParam("code")
        mWxService.wxLogin(code) { user->
            if(null!=user){
                //此时加入的其实只有如下信息：userId，user_token,openid,wxToken
                //先cache到Redis，等到解出用户信息后才一起加入数据库
                user.token=IDGen.strId()
                user.expiredTime = CusUser.format(LocalDateTime.now().plusSeconds(WorkerMaster.getUserTokenTimeout()).toString())//自動延長時間2個小時

                //以用户自定义流程传回的userId作为key
                CusUser.addUser(user)
                rc.response().replyJson(Msg().good(user.toJson()))
            }else
                rc.response().replyJson(Msg().error(Const.ErrUserNotFound()))
        }
    }



    private fun handleWxInfoUser(rc: RoutingContext):Unit{
        var token:String? = null
        var encryptedData :String?=""
        var iv:String? = ""

        rc.bodyAsJson?.let{
            token=it.getString("token",null) //这个token是一般的token，不是wx的token
            encryptedData=it.getString("encryptedData")
            iv=it.getString("iv")
            Log.e("body",rc.bodyAsJson)
        }
        //测试后需要关掉下面这行
       // var result: String? = decrypt(encryptedData, token, iv)

        val user=CusUser.getUser(token)
        if(null==user){
            rc.response().replyJson(Msg().error(105, USER_NOT_LOGIN))
            return
        }

        //////////////// 2、对encryptedData加密数据进行AES解密 ////////////////
        try {
            val result: String? = decrypt(encryptedData, user.wxToken, iv)
            if (null != result && result.length > 0) {
                mHanlder?.invoke(user,Msg(result))

            }
        } catch (e: Exception) {
            e.printStackTrace()
            rc.response().replyJson(Msg().bad("用户信息获取失败"))
        }


        //返回用户数据
        rc.response().replyJson(Msg().good("用户信息已获得"))
    }

    private fun handleWxGetUser(rc: RoutingContext):Unit{
        val token = rc.request().getParam("token")
        if(null==CusUser.getUser(token)){
            rc.response().replyJson(Msg().error(0,USER_FOUND))
            return
        }

        //返回用户数据
        rc.response().replyJson(Msg().good(CusUser.getUser(token)!!.toJson()))
    }



    /**
     * 对于官方加密数据（encryptData）解密说明如下： 加密数据解密算法 接口如果涉及敏感数据（如wx.getUserInfo当中的
     * openId 和unionId ），接口的明文内容将不包含这些敏感数据。开发者如需要获取敏感数据，需要对接口返回的加密数据(
     * encryptedData )进行对称解密。 解密算法如下： 对称解密使用的算法为 AES-128-CBC，数据采用PKCS#7填充。
     * 对称解密的目标密文为 Base64_Decode(encryptedData), 对称解密秘钥 aeskey =
     * Base64_Decode(session_key), aeskey 是16字节 对称解密算法初始向量 iv 会在数据接口中返回。
     *
     * @Description (TODO这里用一句话描述这个方法的作用)
     * @param encryptedData
     * 加密内容
     * @param sessionKey
     * 小程序登录sessionKey
     * @param iv
     * 解密算法初始向量 iv 会在数据接口中返回。
     * @param encodingFormat
     * 编码格式默认UTF-8
     * @return 返回解密后的字符串
     * @throws Exception
     */
    @Throws(Exception::class)
    fun decrypt(encryptedData: String?, sessionKey: String?, iv: String?): String? {
        return try {
            val cipher = Cipher.getInstance("AES/CBC/NoPadding")
            val base64Decoder = BASE64Decoder()
            val _encryptedData = base64Decoder.decodeBuffer(encryptedData)
            val _sessionKey = base64Decoder.decodeBuffer(sessionKey)
            val _iv = base64Decoder.decodeBuffer(iv)
            val secretKeySpec = SecretKeySpec(_sessionKey, "AES")
            val ivParameterSpec = IvParameterSpec(_iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)
            val original = cipher.doFinal(_encryptedData)
            val bytes = decode(original)
            String(bytes, Charset.forName("UTF-8"))
        } catch (ex: Exception) {
            null
        }
    }

}