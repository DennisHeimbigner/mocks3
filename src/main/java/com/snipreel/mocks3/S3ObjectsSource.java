package com.snipreel.mocks3;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

class S3ObjectsSource {
    
    enum Type { MEMORY, FILE };
    
    private static final Logger log = Logger.getLogger(S3ObjectsSource.class.getName());
    
    private static final S3ObjectsSource instance = new S3ObjectsSource();
    static final S3ObjectsSource getInstance () { return instance; }
    
    S3BucketSource getStoreSource (String storeType) {
        return getStoreSource(getType(storeType));
    }
    
    S3BucketSource getStoreSource (Type storeType) {
        switch (storeType) {
            case MEMORY : return new S3BucketMemorySource();
        }
        return NULL_SOURCE;
    }
    
    S3ObjectSource getStore (String storeType) {
        return getStore(getType(storeType));
    }
    
    private Type getType (String storeType) {
        if ( storeType != null && storeType.length() > 0 ) {
            return Type.valueOf(storeType);
        }
        return Type.MEMORY;
    }
    
    S3ObjectSource getStore (Type storeType) {
        switch (storeType) {
            case MEMORY : return new S3ObjectMemorySource();
        }
        return NULL;
    }
    
    static final S3BucketSource NULL_SOURCE = new S3BucketSource () {
        public S3ObjectSource addBucket(String bucket) { return null; }
        public boolean deleteBucket(String bucket)     { return false; }
        public S3ObjectSource getBucket(String bucket) { return null;}
        public List<String> getBucketNames()           { return Collections.<String>emptyList();}
    };
    
    static final S3ObjectSource NULL = new S3ObjectSource () {
        public void addObject(String key,byte[] data) {}
        public byte[] getObject(String key)           { return null; }
        public boolean hasObject(String key)          { return false; }
        public List<String> getKeys ()                { return Collections.<String>emptyList(); }
        public boolean removeObject (String key)      { return false; }
    };

}
