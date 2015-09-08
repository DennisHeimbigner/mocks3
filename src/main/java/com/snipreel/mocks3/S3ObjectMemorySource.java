package com.snipreel.mocks3;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of DataStore that keeps things in memory, meaning each restart of 
 * a servlet loses all previous data.
 *
 */
class S3ObjectMemorySource implements S3ObjectSource {
    
    protected static final NamedCache<byte[]> cache = new NamedCache<byte[]>();
    
    public S3ObjectMemorySource()
    {

    }
    
    public String getName()
    {
        
    }

    public void addObject (String key,byte[] data) {
        if ( data == null )  cache.remove(key);
        else cache.put(key, data);
    }

    public byte[] getObject (String key) { return cache.get(key); }
    public boolean hasObject (String key) { return cache.containsKey(key); }
    public List<String> getKeys () { return cache.getKeys(); }
    public boolean removeObject (String key) { return cache.remove(key); }


    public byte[] getObjectRange(String key, long start, long count)
    {
        if(start < 0 || count <0)
            return null;
        if(count == 0)
            return new byte[0];
        byte[] content = cache.get(key);
        if(content == null) return null;
        long endpoint = start+count;
        long avail = (content.length - start);
        long partial = (avail >= count ? count : avail);
        byte[] data = new byte[(int)count];
        System.arraycopy(data,0,content,(int)start,(int)partial);
        if(partial < count)
            Arrays.fill(data,(int)(start+partial),(int)endpoint,(byte)0);
        return data;
    }

    public boolean setObjectRange(String key, byte[] data, long start, long count)
    {
        if(start < 0 || count < 0)
            return false;
        byte[] content = cache.get(key);
        if(content == null) return false;
        if(count == 0)
            return true;
        long endpoint = start+count;
        if(content.length < endpoint) {//extend
            byte[] newcontent = new byte[(int)endpoint];
            System.arraycopy(content,0,newcontent,0,content.length);
            Arrays.fill(newcontent,content.length,newcontent.length,(byte)0);
            cache.put(key,newcontent);
            content = newcontent;
        }
        System.arraycopy(data,0,content,(int)start,(int)count);
        return true;
    }

    public long getObjectLength(String key)
    {
        byte[] content = cache.get(key);
        if(content == null) return -1;
        return content.length;
    }
}
