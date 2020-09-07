package com.runyu.std

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.ArrayList
import java.lang.reflect.ParameterizedType



object Jos{
    fun analysisClassInfo(o: Any): Class<*> {
        //getGenericSuperclass可以得到包含原始类型,参数化类型,数组,类型变量,基本数据
        val genType = o.javaClass.genericSuperclass
        //获取参数化类型
        val params = (genType as ParameterizedType).actualTypeArguments
        return params[0] as Class<*>
    }
}