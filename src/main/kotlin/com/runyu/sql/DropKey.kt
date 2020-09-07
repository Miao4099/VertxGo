package com.runyu.sql


//class DropKey(private var keys:Array<String> =arrayOf()){
//    fun get():Array<String>{
//        keys=keys.plus("sql")
//        keys=keys.plus("columnNames")
//        keys=keys.plus("numColumns")
//        keys=keys.plus("results")
//        return keys
//    }
//}

class DropKey:Set<String>{
    private var keys:Array<String> =arrayOf()
    init{
        keys=keys.plus("sql")
        keys=keys.plus("columnNames")
        keys=keys.plus("numColumns")
        keys=keys.plus("results")
    }

    override val size: Int
        get() = keys.size

    override fun contains(element: String): Boolean {
        return keys.contains(element)
    }

    override fun containsAll(elements: Collection<String>): Boolean {
        return false
    }

    override fun isEmpty(): Boolean {
        return keys.isEmpty()
    }

    override fun iterator(): Iterator<String> {
        return keys.iterator()
    }
}

//var DropKey=arrayOf("sql","columnNames","numColumns","results")
