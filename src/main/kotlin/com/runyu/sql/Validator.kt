package com.runyu.sql

import java.util.regex.Pattern

class ValException(error:String):Exception(error){

}


abstract class Validator{
    //校验，如果不通过就抛异常，通过的话将值返回，可能会做改变
    abstract fun validate(value:Any?):Any?

    fun match(str:String,regex:String):Boolean {
        val matcher = Pattern.compile(regex).matcher(str)
        return matcher.find()
    }

    fun match(str:String,regex:Regex):Boolean {
        val matcher = Pattern.compile(regex.pattern).matcher(str)
        return matcher.find()
    }
}