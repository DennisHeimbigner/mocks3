/* Copyright Alan Gutierrez 2006 */
package com.snipreel.mocks3;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class S3ConnectionFactory
{
    public final  String domain;
    
    public S3ConnectionFactory(String domain)
    {
        this.domain = domain;
    }
    
    public S3Connection newConnection(String method, String host, String request)
    {
        return new S3Connection(method, host, null, request, new HashMap<String, List<String>>());
    }
    
    public S3Connection newConnection(String method, String host, String bucket, String request)
    {
        return new S3Connection(method, host, bucket, request, new HashMap<String, List<String>>());
    }
    
    public S3Connection newConnection(HttpURLConnection connection)
    {
        Map<String, List<String>> mapOfHeaders = new HashMap<String, List<String>>();
        for (int i = 0; connection.getHeaderField(i) != null; i++)
        {
            String key = connection.getHeaderField(i).toLowerCase();
            if (key == null)
            {
                continue;
            }
            List<String> listOfValues = mapOfHeaders.get(key);
            if (listOfValues == null)
            {
                listOfValues = new ArrayList<String>();
                mapOfHeaders.put(key, listOfValues);
            }
            listOfValues.add(connection.getHeaderField(i));
        }
        String method = connection.getRequestMethod();
        String host = connection.getHeaderField("Host");
        if (host == null)
        {
            host = connection.getURL().getHost();
        }
        String request = getRequest(connection.getURL());
        String bucket = getBucket(host, request);
        return new S3Connection(method, host, bucket, request, Collections.unmodifiableMap(mapOfHeaders));
    }
    
    private String getRequest(URL url)
    {
        StringBuilder builder = new StringBuilder();
        if (url.getPath() == null){
            builder.append('/');
        }else {
        builder.append(url.getPath());}
        if (url.getQuery() != null)
        {
            builder.append('?').append(url.getQuery());
        }
        return builder.toString();
    }
    
    private String getBucket(String host, String request)
    {
        if (host.equals(domain))
        {
            if (request.equals("/") || request.startsWith("/?"))
            {
                return null;
            }
            return host.substring(1, host.indexOf('/', 1));
        }
        else if (host.endsWith('.' + domain))
        {
            return host.substring(0, host.lastIndexOf('.' + domain));
        }
        return host;
    }
}

/* vim: set et sw=4 ts=4 ai tw=78 nowrap: */