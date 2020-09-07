package com.runyu.std


import io.vertx.core.json.JsonObject
import java.io.File

class Config(var cfgName:String,var path:String=System.getProperty("user.dir")+"/config"){
    fun readText(): String {
        val currentDir = path+"/"
        val file = File(currentDir, cfgName)
        var config = "{}"
        try {
            config = file.readText()
            Log.e("Config", "read $cfgName success")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Config", "read $cfgName fail")
        }
        return config
    }

    fun readJson():JsonObject{
        return JsonObject(readText())
    }
}

