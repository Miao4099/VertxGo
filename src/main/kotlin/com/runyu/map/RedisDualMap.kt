package com.runyu.map



class RedisDualMap<T>(cfgName:String,var clazz: Class<*>){
    private var mMap1= Redis(cfgName)
    private var mMap2= Redis(cfgName)



    fun put(k1:String,k2:String,value:T){
        mMap1.put(k1,k2)
        mMap2.putObj(k2,value as Any)
    }

    /**
     * key可能是key1或key2
     */
    fun clear(key:String){
        if(mMap1.containsKey(key)){
            mMap2.del(mMap1.get(key)!!)
            mMap1.del(key)
        }else
            mMap2.del(key)
    }

    fun getByK1(k1:String):T?{
        mMap1.get(k1)?.let{
            return mMap2.getObj(it,clazz) as T
        }
        return null
    }

    fun getByK2(k2:String):T?{
        return mMap2.getObj(k2,clazz) as T?
    }
}