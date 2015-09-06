package com.snipreel.mocks3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread Safe cache of strings to an object.  
 * Basically, this is just a wrapper around a ConcurrentHashMap to provide
 * a class where I can add any necessary thread safety tests (though currently
 * there aren't any) and to provide the string keys as a sorted list.
 * @author paul
 *
 * @param <T>
 */
class NamedCache<T> {
    
    private ConcurrentHashMap<String, T> cache = new ConcurrentHashMap<String,T>();
    
    boolean remove (String key)       {
        return cache.remove(key) != null;
    }
    T get (String key)                { return cache.get(key); }
    void put (String key, T t)        { cache.put(key, t); }
    T putIfAbsent(String key, T t)    { return cache.putIfAbsent(key, t); }
    boolean containsKey (String key)  { return cache.containsKey(key); }
    
    List<String> getKeys () {
        List<String> result = new ArrayList<String>();
        result.addAll(cache.keySet());
        Collections.sort(result);
        return result;
    }

}
