package com.runyu.std

open class SysErr(var code:Int, var msg:String)

class ErrByParam(msg:String):SysErr(1001,"parameter missed: $msg"){

}