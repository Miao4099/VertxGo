package com.runyu.std

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import org.apache.logging.log4j.core.config.Configurator
import org.apache.logging.log4j.core.config.ConfigurationSource
import java.io.FileInputStream
import java.io.BufferedInputStream
import java.io.File




/**
 * Created by sheng on 2018/12/28.
 */

object Log{


    private var logger:Logger?=null
    private var out= ByteArrayOutputStream()
    private var ps=PrintStream(out)

    fun reload(cfgFile:String?=null){
        try {
            val file = File(cfgFile)
            val s = BufferedInputStream(FileInputStream(file))
            val source = ConfigurationSource(s)
            Configurator.initialize(null, source)

            logger = LogManager.getLogger(this.javaClass.name)
        }catch (e:Exception){
            logger = LogManager.getLogger(this.javaClass.name)
        }
    }


    @Synchronized fun e(tag:String,vararg args:Any?){
        args?.let {
            ps.print("$tag :")
            args.forEach { ps.print(" ");ps.print(it) }
            logger?.error(out)
            out.reset()
            return
        }
        logger?.error("$tag : null")
    }




}
