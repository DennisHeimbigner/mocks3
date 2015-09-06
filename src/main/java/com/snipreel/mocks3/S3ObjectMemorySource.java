package com.snipreel.mocks3;

import java.util.List;

/**
 * Implementation of DataStore that keeps things in memory, meaning each restart of 
 * a servlet loses all previous data.
 *
 */
class S3ObjectMemorySource implements S3ObjectSource {
    
    private static final NamedCache<byte[]> cache = new NamedCache<byte[]>();

    public void addObject (String key,byte[] data) {
        if ( data == null )  cache.remove(key);
        else cache.put(key, data);
    }

    public byte[] getObject (String key) { return cache.get(key); }
    public boolean hasObject (String key) { return cache.containsKey(key); }
    public List<String> getKeys () { return cache.getKeys(); }
    public boolean removeObject (String key) { return cache.remove(key); }
    
}
