package com.snipreel.mocks3;

import javax.servlet.http.HttpServletRequest;

class S3Info {
    
    S3Info (HttpServletRequest request) {
        this(request.getLocalName(), request.getRequestURI()); 
    }

    private S3Info (String serverName, String requestUri) {
        requestUri = removeLeadingSlash(requestUri);
        if ( isAmazonServer(serverName) ) {
            int index = requestUri.indexOf("/");
            this.bucket = requestUri.substring(0, index);
            this.key = requestUri.substring(index+1);
        } else {
            int index = serverName.indexOf(".");
            this.bucket = serverName.substring(0, index);
            this.key = requestUri;
        }
    }
    
    private static String removeLeadingSlash (String input) {
        if ( input.charAt(0) == '/') return input.substring(1);
        else return input;
    }
    
    private static boolean isAmazonServer (String serverName) {
        return serverName.split("\\.").length == 3;
    }
    
    private final String bucket;
    private final String key;
    
    String getBucket () { return bucket; }
    String getKey ()    { return key; }
    
    @Override
    public boolean equals (Object o) {
        if ( this == o ) return true;
        if ( !(o instanceof S3Info) ) return false;
        S3Info that = (S3Info)o;
        return this.bucket.equals(that.bucket) && this.key.equals(that.key);
    }
    
    @Override
    public int hashCode () {
        return 17*this.bucket.hashCode() + 29*this.key.hashCode();
    }

}
