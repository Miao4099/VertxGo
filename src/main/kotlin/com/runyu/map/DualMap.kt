package com.runyu.map



public class DualMap<K1, K2, V>{

    private var mMap1= mutableMapOf<K1,K2>()
    private var mMap2= mutableMapOf<K2,V>()


    fun put(k1:K1,k2:K2,value:V){
        mMap1.put(k1,k2)
        mMap2.put(k2,value)
    }

    fun clear(k2:K2){
        mMap2.remove(k2)
    }

    fun getByK1(k1:K1):V?{
        mMap1.get(k1)?.let{
            return mMap2.get(it)
        }
        return null
    }

    fun getByK2(k2:K2):V?{
         return mMap2.get(k2)
    }

    fun forEach(block:(key:K2,value:V)->Unit){
        mMap2.forEach(block)
    }
}