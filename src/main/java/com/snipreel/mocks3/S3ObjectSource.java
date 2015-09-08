package com.snipreel.mocks3;

import java.util.List;

/**
 * Used by MockS3Servlet to store and retrieve bytes from somewhere.
 * NOTE: This does not include "put-if-absent" semantics, as the 
 * S3 service does not support that.
 */
interface S3ObjectSource {

    /**
     * Get Bucket id
     */
    public String getName();
    
    /**
     * Retrieve the bytes stored with the provided key.  Returns null if the key does not exist.
     */
    public byte[] getObject (String key);

    /**
     * Store the provided data for the provided key.  Overwrites data if already specified.
     * Providing null data removes the value for the key. 
     */
    public void addObject (String key, byte[] data);
    
    /**
     * Return whether there is currently data available for the given key.
     */
    public boolean hasObject (String key);
    
    /**
     * Returns the keys in this store sorted alphabetically
     */
    public List<String> getKeys ();
    
    /**
     * Delete the identified object, returning true if it existed and false if it didn't.
     */
    public boolean removeObject (String key);

    /**
     * Retrieve a range of bytes stored with the provided key.  Returns null if the key does not exist.
     */
    public byte[] getObjectRange(String key, long start, long count);

    /**
     * Write a range of bytes stored with the provided key.  Returns false if the key does not exist.
     */
    public boolean setObjectRange(String key, byte[] data, long start, long count);

    /**
     * Get current length of the object; Returns -1 if the key does not exist.
     */
    public long getObjectLength(String key);

}
