package com.snipreel.mocks3;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystemNotFoundException;
import java.util.*;

class S3BucketFileSource implements S3BucketSource
{
    static protected final String ROOTPROPERTY = "mocks3.file.root";

    protected String rootname = null;
    protected File root = null;

    public S3BucketFileSource()
    {
        this.rootname = System.getProperty(ROOTPROPERTY).trim();
        if(this.rootname == null || this.rootname.length() == 0)
            throw new IllegalArgumentException("S3BucketFileSource: no file root specified");
        if(this.rootname.endsWith("/"))
            this.rootname = this.rootname.substring(0, this.rootname.length() - 1);
        // Make sure root dir exists
        root = new File(rootname);
        checkfile(root, true);
    }

    public S3ObjectSource addBucket(String bucket)
    {
        if(bucket == null || bucket.length() == 0)
            throw new IllegalArgumentException();
        File b = new File(root, bucket);
        checkfile(b, true);
        S3ObjectFileSource store = (S3ObjectFileSource) S3ObjectsSource.getInstance().getStore(S3ObjectsSource.Type.FILE);
        store.initialize(root, b);
        return store;
    }

    public boolean deleteBucket(String bucket)
    {
        File b = new File(root, bucket);
        clearbucket(b);
        return b.delete();
    }

    public S3ObjectSource getBucket(String name)
    {
        File b = new File(root, name);
        if(!b.exists())
            b.mkdir();
        if(!b.canWrite() || !b.canRead())
            throw new IllegalArgumentException("Bucket file: cannot read/write: " + name);
        return null;
    }

    public List<String> getBucketNames()
    {
        return null;
    }

    //////////////////////////////////////////////////

    static public void
    checkfile(File f, boolean isdir)
    {
        String name = f.getName();
        if(!f.exists()) {
            if(isdir) {
                try {
                    if(!f.mkdir())
                        throw new IllegalArgumentException("S3BucketFileSource: cannot create directory: " + name);
                    else if(!f.createNewFile())
                        throw new IllegalArgumentException("S3BucketFileSource: cannot create file: " + name);
                } catch (IOException ioe) {
                    throw new IllegalArgumentException("S3BucketFileSource: cannot create file: " + name, ioe);

                }
            }
        } else if(isdir && !f.isDirectory())
                throw new IllegalArgumentException("S3BucketFileSource: file is not a directory: " + name);
        if(!f.canRead() && !f.canWrite())
            throw new IllegalArgumentException("S3BucketFileSource: cannot read/write file: " + name);
    }

    static public boolean
    clearbucket(File b)
    {
        String[] contents = b.list();
        if(contents.length == 0) return true;
        for(String file : contents) {
            File f = new File(file);
            if(!f.delete()) return false;
        }
        return true;
    }

}
