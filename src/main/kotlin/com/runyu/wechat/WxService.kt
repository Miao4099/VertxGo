package com.runyu.wechat

import com.runyu.com.runyu.wechat.msg.app.WxSubscribeMsg
import com.runyu.http.HttpClient
import com.runyu.map.Cache
import com.runyu.std.Log
import com.runyu.std.Msg
import com.runyu.user.User
import io.vertx.core.json.JsonObject
import okhttp3.RequestBody
import org.apache.http.client.methods.RequestBuilder
import java.time.LocalDateTime

class WxService(var appId:String,var secret:String) {
    var mTokenMap= Cache("WxService")
    var mAccessTokenTime:LocalDateTime?=null

    /**
     * 返回的是
     * {"access_token":"ACCESS_TOKEN",
        "expires_in":7200,
        "refresh_token":"REFRESH_TOKEN",
        "openid":"OPENID",
        "scope":"SCOPE",
        "unionid": "unionid"
    }
     */

    private fun getAccessToken(block:(token:String)->Unit){
        var token=""
        if(null==mAccessTokenTime||LocalDateTime.now().minusHours(2)>=mAccessTokenTime){
            wxGetAccessToken {json->
                mAccessTokenTime=LocalDateTime.now()
                token=Msg(json).getString("access_token")
                mTokenMap.put(appId,token)
                block.invoke(token)
                Log.e("getAccessToken",json)
            }
        }else
            block.invoke(mTokenMap.get(appId) as String)
    }

    private fun wxGetAccessToken(block:(json:JsonObject?)->Unit){
        var url="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=$appId&secret=$secret"
        HttpClient().get(url){success,response->
            Log.e("response=>",response?:"")
            var finalOK=false
            if(success){
                var rsp=Msg(response!!)
                if(!rsp.containsKey("errcode")){
                    finalOK=true
                    block.invoke(rsp)
                }
            }

            if(!finalOK) {
                Log.e("wxGetToken",response?:"null")
                block.invoke(null)
            }

        }

    }

    private fun wxGetToken(code:String,block:(json:JsonObject?)->Unit){
        var url=" https://api.weixin.qq.com/sns/jscode2session?appid=$appId&secret=$secret&js_code=$code&grant_type=authorization_code"
        HttpClient().get(url){success,response->
            Log.e("response=>",response?:"")
            var finalOK=false
            if(success){
                var rsp=Msg(response!!)
                if(!rsp.containsKey("errcode")){
                    finalOK=true
                    block.invoke(rsp)
                }
            }

            if(!finalOK) {
                Log.e("wxGetToken",response?:"null")
                block.invoke(null)
            }

        }

    }

    private fun wxGetUserInfo(token:String,openid:String,block:(json:JsonObject?)->Unit){
        var url="https://api.weixin.qq.com/sns/userinfo?access_token=$token&openid=$openid"

        HttpClient().get(url){success,response->
            var finalOK=false
            if(success){
                var rsp=Msg(response!!)
                if(!rsp.containsKey("errcode")){
                    finalOK=true
                    block.invoke(rsp)
                }
            }

            if(!finalOK) {
                Log.e("wxGetUserInfo",response?:"null")
                block.invoke(null)
            }

        }
    }




    fun wxLogin(code:String,block:(user: User?)->Unit){
        //根据code拿token，根据token再得到用户信息
        wxGetToken(code) { json->
            //应该是这样的 {"session_key":"nA+kxmxL3X0Dd9+IGxcvZA==","openid":"oLsK-4hHkYiaWpRX41M_LWzSlK8A"}
            if(null!=json) {
                Log.e("wxLogin", "step1:get token", json!!)
                var token = json?.getString("session_key")
                var openid = json?.getString("openid")

                if (null != token && null != openid) {
                    var user = User()
                    user.openid = openid
                    user.user_id = getUserId(openid)
                    user.wxToken = token
                    Log.e("wxLogin", "success to get user openid=$openid")
                    block.invoke(user)
                } else {
                    Log.e("wxLogin", "fail to get user openid")
                    block.invoke(null)
                }
            }else{
                Log.e("wxLogin", "fail to get user openid...")
                block.invoke(null)
            }
        }

    }



    //https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET


    fun wxSendSubscribeMsg(msg:WxSubscribeMsg,block:(success:Boolean,Msg)->Unit){
        wxSendSubscribeMsg(msg.build(),block)
    }

    fun wxSendSubscribeMsg(msg:JsonObject,block:(success:Boolean,Msg)->Unit){
        getAccessToken {token->
            var url="https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=$token"
            HttpClient().postJson(url,msg) { success, rsp->
                if(success){
                    if(0==Msg(rsp!!).getInteger("errcode")){
                        Log.e("wxSendSubscribeMsg","success to ${msg.getString("touser")}")
                        block(true,Msg())
                    }else {
                        Log.e("wxSendSubscribeMsg","fail to ${msg.getString("touser")}",msg.toString())
                        block(false, Msg(rsp))
                    }
                }
            }
        }
    }

    fun getUserId(openid:String):String{
        return openid+"_001"
    }




}