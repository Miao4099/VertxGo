package com.runyu.html



/**
 * Created by sheng on 2017/7/15.
 */
abstract class CardHtml<T>: Html<T>(){
    lateinit var mCards:HashMap<Int,kotlinx.html.UL.()->Unit>
    init {
        mCards = HashMap<Int, kotlinx.html.UL.() -> Unit>()
    }

    fun buildCard(block: kotlinx.html.UL){
        for(i in 0..mCards.size-1) {
            mCards[i]?.invoke(block)
        }
    }

    fun addCard(block: kotlinx.html.UL.()->Unit): CardHtml<T> {
        mCards.put(mCards.size,block)
        return this
    }
}