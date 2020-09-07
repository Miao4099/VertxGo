package com.runyu.http

import com.runyu.std.Log
import io.vertx.core.json.JsonObject
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


class HttpClient{
    var JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
    private val mClient = OkHttpClient()

    fun get(url:String,cb:(success:Boolean,rsp:String?)->Unit){
        Log.e("HttpClient",url)
        val request = Request.Builder()
                .url(url)
                .build()

        request(request,cb)
    }

    fun postForm(url:String,formBody:FormBody,cb:(success:Boolean,rsp:String?)->Unit){
        val request = Request.Builder()
                .url(url)
                .post(formBody)
                .build()

        request(request,cb)
    }


    fun postJson(url:String,json:JsonObject,cb:(success:Boolean,rsp:String?)->Unit){
        val request: Request = Request.Builder()
                .url(url)
                .post(json.toString().toRequestBody(JSON))
                .build()

        request(request,cb)
    }

    fun request(request:Request,cb:(success:Boolean,rsp:String?)->Unit){

        mClient.newCall(request).enqueue(object:Callback {
            override fun onResponse(call: Call, response: Response) {
                var rsp=response.body?.string()//只能读一次
                cb.invoke(true,rsp)
            }
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                cb.invoke(false,e.message!!)
            }
        })
    }
}
