package com.snipreel.mocks3;

import java.util.List;

class S3BucketMemorySource implements S3BucketSource {
    
    S3BucketMemorySource () {
    }

    private static final NamedCache<S3ObjectSource> cache = new NamedCache<S3ObjectSource>();

    public S3ObjectSource addBucket(String bucket) {
        S3ObjectSource store = S3ObjectsSource.getInstance().getStore(S3ObjectsSource.Type.MEMORY);
        S3ObjectSource added = cache.putIfAbsent(bucket, store);
        if ( added == store ) return added;
        else return null;
    }

    public boolean deleteBucket(String bucket) { return cache.remove(bucket); }
    public S3ObjectSource getBucket(String bucket) { return cache.get(bucket); }
    public List<String> getBucketNames() { return cache.getKeys(); }
}
