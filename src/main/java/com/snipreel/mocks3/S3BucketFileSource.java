package com.snipreel.mocks3;

import java.io.File;
import java.nio.file.FileSystemNotFoundException;
import java.util.List;
import java.util.Properties;

class S3BucketFileSource implements S3BucketSource
{
    protected String root = null;

    public S3BucketFileSource
    {
        this.root = params.getProperty("root").trim();
        if(this.root == null || this.root.length() == 0)
            throw new S3Exception("S3FileStore: no file root specified");
        if(this.root.endsWith("/"))
            this.root = this.root.substring(0, this.root.length() - 1);
    }

    public S3Bucket addBucket(String bucket, Properties params)
    {
        if(bucket == null || bucket.length() == 0)
            throw new IllegalArgumentException();

        S3FileBucket b = getBucket(bucket);
        if(b == null) b = addBucket(bucket,params);
    }

    public boolean deleteBucket(String bucket)
    {
        return false;
    }

    public S3FileBucket getBucket(String name)
    {
        StringBuilder bucketpath = new StringBuilder();
        bucketpath.append(this.root);
        bucketpath.append("/");
        bucketpath.append(name);
        File b = new File(bucketpath.toString());
        if(!b.exists())
            b.mkdir();
        if(!b.canWrite() || !b.canRead())
            throw new S3Exception("Bucket file: cannot read/write");
        return null;
    }

    public List<String> getBucketNames()
    {
        return null;
    }
}
