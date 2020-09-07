package com.runyu.html

import kotlinx.html.*
import kotlinx.html.stream.appendHTML



abstract class Html<T> {
    protected var mTitle="runyu"
    protected lateinit var mCss:HashMap<Int,String>
    protected lateinit var mJs:HashMap<Int,String>
    protected var mBodyBlock: BODY.() -> String={""}

    init{
        mCss=HashMap<Int,String>()
        mJs=HashMap<Int,String>()
    }

    fun title(title:String): Html<T> {
        mTitle=title
        return this
    }
    fun addCss(cssName:String): Html<T> {
        mCss.put(mCss.size,cssName)
        return this
    }
    fun addJs(name:String): Html<T> {
        mJs.put(mJs.size,name)
        return this
    }

    private fun buildCss(block: kotlinx.html.HEAD): kotlin.Unit {
        for(i in 0..mCss.size-1) {
            var name=mCss[i]!!
            block.link {
                rel = LinkRel.stylesheet
                type= LinkType.textCss
                href = name
            }
        }
    }

    private fun buildJs(block: kotlinx.html.HEAD): kotlin.Unit {
        for(i in 0..mJs.size-1) {
            var name=mJs[i]!!
            block.script {
                type= ScriptType.textJavaScript
                src=name
            }
        }
    }


    open fun addBody(block : BODY.() -> String,classes : String? = null): Html<T> {
        mBodyBlock=block
        return this
    }


    fun build():String {
        var text = buildString {
            appendln("<!DOCTYPE Html>")
            appendln("<Html lang=\"zh\">")
            appendHTML().html {
                head {
                    title ( mTitle )
                    meta("charset","utf-8")
                    meta("viewport", "width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0")
                    buildCss(this)
                    buildJs(this)
                }
                body{
                    appendln(mBodyBlock.invoke(this))
                }
            }
            appendln()
        }
        return text
    }
}