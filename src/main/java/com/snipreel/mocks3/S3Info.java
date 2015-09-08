package com.snipreel.mocks3;

import com.sun.deploy.net.HttpRequest;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

class S3Info
{

    protected HttpServletRequest request;
    protected URI requestURI;

    S3Info(HttpServletRequest request)
    {
        this.request = request;
        String requesturi = request.getRequestURI();
        requesturi = removeLeadingSlash(requesturi);
        try {
            this.requestURI = new URI(requesturi);
        } catch (URISyntaxException use) {
            throw new IllegalArgumentException("Malformed request URI: " + requesturi);
        }
        // We assume that urls are of the form: http://bucket.hostname:port/object
        String hostname = getHeader("Host");
	if(hostname == null)
	    throw new IllegalArgumentException("Missing Host: header");
        int index = host.indexOf('.');	
	if(index < 0)
	    throw new IllegalArgumentException("Missing bucket id");
	this.bucket = host.substring(0,index);
        String path = this.requestURI.getPath();
	if(path.startsWith("/"))
	    path = path.substring(1);
        this.key = path;
    }

    protected static String removeLeadingSlash(String input)
    {
        if(input.charAt(0) == '/') return input.substring(1);
        else return input;
    }

    protected String bucket;
    protected String key;

    String getBucket()
    {
        return bucket;
    }

    String getKey()
    {
        return key;
    }

    public String getHeader(String hdr)
    {
        return request.getHeader(hdr);
    }

    public HttpServletRequest getRequest()
    {
        return request;
    }

    public URI getRequestURI()
    {
        return requestURI;
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o) return true;
        if(!(o instanceof S3Info)) return false;
        S3Info that = (S3Info) o;
        return this.bucket.equals(that.bucket) && this.key.equals(that.key);
    }

    @Override
    public int hashCode()
    {
        return 17 * this.bucket.hashCode() + 29 * this.key.hashCode();
    }

}
