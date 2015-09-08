package com.snipreel.mocks3;

import java.io.*;
import java.util.*;

/**
 * Implementation of DataStore that keeps things in the file system.
 */
class S3ObjectFileSource implements S3ObjectSource
{

    protected File root = null;
    protected File bucket = null;

    public S3ObjectFileSource()
    {
    }

    public void initialize(File root, File bucket)
    {
	this.root = root;
	this.bucket = bucket;
    }

    public String getName()
    {
        return this.bucket.getName();
    }

    public void addObject(String key, byte[] data)
    {
	File o = new File(bucket,key);
	S3BucketFileSource.checkfile(o,false);
	try {
	    FileOutputStream fos = new FileOutputStream(o);
	    fos.write(data);
            fos.close();
	} catch (IOException ioe) {
	    throw new IllegalArgumentException("S3ObjectFileSource",ioe);
	}
    }

    public byte[] getObject(String key)
    {
	File o = new File(bucket,key);
	if(!o.exists() || !o.canRead())
	    return null;
	try {
	    long length = o.length();
	    byte[] contents = new byte[(int)length];
	    FileInputStream fis = new FileInputStream(o);
	    int offset = 0;
	    long toread = length;
	    while(toread > 0) {
		int red = fis.read(contents,offset,(int)toread);
		if(red < 0)
		    throw new IllegalArgumentException("S3ObjectFileSource: unexpected eof");
		offset += red;
		toread -= red;
	    }
	    return contents;
	} catch (IOException ioe) {
	    throw new IllegalArgumentException(ioe);
	}
    }

    public long getObjectLength(String key)
    {
        File o = new File(bucket,key);
        if(!o.exists() || !o.canRead())
            return -1;
        return o.length();
    }

    public byte[] getObjectRange(String key, long start, long count)
    {
        if(start < 0 || count <0)
            return null;
        if(count == 0)
            return new byte[0];
        File o = new File(bucket,key);
        if(!o.exists() || !o.canRead())
            return null;
        long endpoint = start+count;
        byte[] data = new byte[(int)(count)];
        try {
            try (FileInputStream f = new FileInputStream(o)) {
                long avail = o.length() - start;
                if(avail < 0) return null;
                // Seek to proper start position
                if(f.skip(start) < start)
                    return null;
                long want = count;
                long offset = 0;
                long toread = (want > avail ? avail : want);
                while(toread > 0) {
                    int red = f.read(data, (int) offset, (int) toread);
                    if(red < 0) break;
                    toread -= red;
                    offset += red;
                }
                if(toread > 0) // short read
                    return null;
                // zero fill if necessary
                if(want > avail)
                    Arrays.fill(data,(int)avail,(int)want,(byte)0);
            }
        } catch (IOException e) {
            return null;
        }
        return data;
    }

    public boolean setObjectRange(String key, byte[] data, long start, long count)
    {
        if(start < 0 || count <0)
            return false;
        if(count == 0)
            return true;
        File o = new File(bucket,key);
        if(!o.exists() || !o.canRead())
            return false;
        try {
            try (RandomAccessFile f = new RandomAccessFile(o,"rw")) {
                if(f.skipBytes((int)start) < start)
                    return false;
                f.write(data, (int) start, (int) count);
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public boolean hasObject(String key)
    {
	File o = new File(bucket,key);
	return o.exists();
    }

    public List<String> getKeys()
    {
	String[] objects = bucket.list();
	List<String> list = new ArrayList<>();
	for(String s: objects) list.add(s);
	return list;
    }

    public boolean removeObject(String key)
    {
	File o = new File(bucket,key);
	if(!o.exists()) return false;
        return o.delete();
    }

}
