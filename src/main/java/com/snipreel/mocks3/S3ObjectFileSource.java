package com.snipreel.mocks3;

import java.util.List;

/**
 * Implementation of DataStore that keeps things in the file system.
 */
class S3ObjectFileSource implements S3ObjectSource
{

    protected String root = null;

    public S3ObjectFileSource()
    {
    }

    public void addObject(String key, byte[] data)
    {
    }

    public byte[] getObject(String key)
    {
        return null;
    }

    public boolean hasObject(String key)
    {
        return false;
    }

    public List<String> getKeys()
    {
        return null;
    }

    public boolean removeObject(String key)
    {
        return false;
    }

}
