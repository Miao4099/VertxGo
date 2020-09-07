package com.runyu.std

import java.time.ZonedDateTime

fun now():String{
    return ZonedDateTime.now().toString().replace("T"," ").substring(0,19)
}
fun today():String{
    return now().substring(0,10)
}
fun tomorrow():String{
    return ZonedDateTime.now().plusDays(1).toString().replace("T"," ").substring(0,19)
}
