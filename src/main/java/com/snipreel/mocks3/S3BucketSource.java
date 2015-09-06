package com.snipreel.mocks3;

import java.util.List;

/**
 * Interface for retrieving data stores based on bucket names
 */
interface S3BucketSource {

    /**
     * Retrieve the data store for the bucket, null if there is no such bucket
     */
    S3ObjectSource getBucket (String bucket);
    
    /**
     * Add a data store for the provided bucket, if it doesn't already exist.
     * If the named bucket already exists, this will return null
     * @param bucket
     * @return
     */
    S3ObjectSource addBucket (String bucket);
    
    /**
     * Delete the named store.
     */
    boolean deleteBucket (String bucket);
    
    /**
     * Retrieve a list of the bucket names for which stores exist
     */
    List<String> getBucketNames ();

}
