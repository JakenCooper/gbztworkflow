package com.gbzt.gbztworkflow.utils;

/***
 *
 *  弱智向数据缓存，仅仅用于数据的临时保存
 */

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
public class SimpleCache {

    private static final Map<String,Object> cache;

    static{
        cache = new ConcurrentHashMap<String,Object>();
    }

    public static void putIntoCache(String key,Object value){
        synchronized (SimpleCache.class){
            if(cache.containsKey(key)){
                cache.remove(key);
            }
            cache.put(key,value);
        }
    }

    public static Object getFromCache(String key){
        synchronized (SimpleCache.class){
            return cache.get(key);
        }
    }

    public static boolean inCache(String key){
        synchronized (SimpleCache.class){
            return cache.containsKey(key);
        }
    }

    public static void remove(String key){
        synchronized (SimpleCache.class){
            if(cache.containsKey(key)){
                cache.remove(key);
            }
        }
    }

}
