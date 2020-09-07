package com.runyu.common.oss

import com.aliyun.oss.OSS
import com.aliyun.oss.OSSClientBuilder
import com.runyu.map.Cache
import java.util.*

class OssClient{
    var mCfg:AliOssConfig?=null
    var mOssClient: OSS?=null
    var mCache: Cache?=null

    fun init(cfg:AliOssConfig):OssClient{
        mCfg=cfg
        mOssClient = OSSClientBuilder().build(cfg.endpoint, cfg.accessKeyId, cfg.accessKeySecret)
        mCache=Cache(cfg.endpoint+"_"+cfg.bucket)
        return this
    }
    fun tomorrow(): Date {
        return Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 1) //一天
    }

    fun year50(): Date {
        return Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 20000) //50年
    }

    fun signatureUrl(name:String?,mode:Int=0):String{
        if(!name.isNullOrEmpty())
            when(mode) {
                1 -> return "http://xxx.oss-cn-shenzhen.aliyuncs.com/" + name
                2 -> return "http://yyy.oss-cn-shenzhen.aliyuncs.com/" + name
                else -> {

                    if (null == mCache!!.get(name))
                        mCache!!.put(name, mOssClient?.generatePresignedUrl(mCfg!!.bucket, name, year50()).toString())
                    return mCache!!.get(name) as String
                }
        }
        return ""
    }


    //因为上传后的文件名不一样，所以name变化就会导致原来的cache基本不用清除，这个接口只是备用
    fun clearCache(name:String?){
        name?.let{
            mCache!!.remove(name)
        }
    }
}