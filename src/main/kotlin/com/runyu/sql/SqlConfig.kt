package com.runyu.sql

import java.io.File

class SqlConfig(var filename:String, var dbName:String?=null) {
    private fun getConfigPath():String{
        return System.getProperty("user.dir") + "/config/sql"
    }

    fun readCfg(cfgName:String):String{
        val currentDir = getConfigPath()
        val file = File(currentDir, cfgName+".sql")
        var config=""
        try {
            config = file.readText()
        }catch (e:Exception){
            e.printStackTrace()
        }
        return config
    }


    fun toSqlArray():Array<String>{
        var content=readCfg(filename)
        if(null==content){
            return arrayOf()
        }

        var arr=arrayOf<String>()
        if(!dbName.isNullOrEmpty()) {
            arr = arr.plus("CREATE DATABASE IF NOT EXISTS $dbName DEFAULT CHARSET utf8 COLLATE utf8_general_ci")
            arr = arr.plus("USE $dbName")
        }

        removeNoneSense(content).split(";").forEach {
            if(!it.isNullOrEmpty()) {
               arr = arr.plus(it)
            }
        }
       return arr
    }

    fun removeNoneSense(content:String):String{
        return content.replace("/\\*{1,2}[\\s\\S]*?\\*/".toRegex(),"")
                .replace("(''.*?''|\".*?\")|/\\*.*?\\*/|--.*?(?=[\\r\\n]|\$)".toRegex(),"")
                .replace("^\r?\$(\n|\r\n)","")
                .replace("\r\n","")

    }
}