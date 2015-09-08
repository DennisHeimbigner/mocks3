package com.snipreel.mocks3;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class S3RequestFactory
{
    static final Charset UTF8 = Charset.forName("UTF-8");

    protected static Logger log = Logger.getLogger(S3RequestFactory.class.getName());


    S3RequestFactory(S3BucketSource bucketSource)
    {
        this.bucketSource = bucketSource;
    }

    protected final S3BucketSource bucketSource;

    S3RequestHandler getHandler(HttpServletRequest req, HttpServletResponse rsp)
    {
        S3Info info = new S3Info(req);
        BaseHandler handler = getBaseHandler(req.getMethod(), info);
        handler.initialize(this.bucketSource, info, rsp);
        return handler;
    }

    BaseHandler getBaseHandler(String reqMethod, S3Info info)
    {
        if(reqMethod.equalsIgnoreCase("get")) {
            if(info.getKey().length() > 0) {
                return new S3ObjectGetter(true);
            } else if(info.getBucket().length() > 0) {
                return new S3ObjectLister();
            } else {
                return new S3BucketLister();
            }
        } else if(reqMethod.equalsIgnoreCase("head")) {
            if(info.getKey().length() > 0) {
                return new S3ObjectGetter(false);
            } else if(info.getBucket().length() > 0) {
                return new S3BucketChecker();
            }
        } else if(reqMethod.equalsIgnoreCase("post")) {
        } else if(reqMethod.equalsIgnoreCase("put")) {
        } else if(reqMethod.equalsIgnoreCase("delete")) {
            if(info.getKey().length() > 0) {
                return new S3ObjectDeleter();
            } else if(info.getBucket().length() > 0) {
                return new S3BucketDeleter();
            }
        }
        return INVALID_METHOD;
    }

    protected static void writeResponse(HttpServletResponse rsp, byte[] data)
    {
        try {
            OutputStream os = rsp.getOutputStream();
            os.write(data);
            os.flush();
        } catch (IOException ex) {
            log.warning("IO Problem writing to response");
        }
    }

    public static abstract class BaseHandler implements S3RequestHandler
    {
        protected S3Info requestInfo = null;
        protected HttpServletResponse response = null;
        protected S3BucketSource bucketSource = null;
        protected String urlprefix = null;

        public void Initialize(S3BucketSource bucketsource, S3Info info, HttpServletResponse rsp)
        {
            this.bucketSource = this.bucketSource;
            this.requestInfo = info;
            this.response = rsp;
            StringBuilder buf = new StringBuilder();
            buf.append("http://");
            URI requri = requestInfo.getRequestURI();
            buf.append(requri.getHost());
            if(requri.getPort() > 0)
                buf.append(String.format(":%d", requri.getPort()));
            buf.append("/");
            this.urlprefix = buf.toString();
        }

        public S3Info getRequest()
        {
            return requestInfo;
        }

        protected S3ObjectSource getStore(String bucket)
        {
            return bucketSource.getBucket(bucket);
        }
    }

    protected static class S3ObjectGetter extends BaseHandler
    {

        S3ObjectGetter(boolean includeBody)
        {
            this.includeObjectBody = includeBody;
        }

        protected final boolean includeObjectBody;

        public void doit()
        {
            S3ObjectSource store = getStore(requestInfo.getBucket());
            if(store == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            } else {
                // See if we have a range header
                String range = requestInfo.getHeader("Range");
                byte[] data = null;
                boolean malformed = false;
                if(range == null) {
                    data = store.getObject(requestInfo.getKey());
                } else {
                    // Parse the range
                    Range r = parseRange(range, store.getObjectLength(requestInfo.getKey()));
                    if(r != null)
                        data = store.getObjectRange(requestInfo.getKey(), r.start, r.count);
                    if(r == null)
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    else if(data != null) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        if(includeObjectBody) writeResponse(response, data);
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    }
                }
            }
        }
    }

    protected static class S3ObjectLister extends BaseHandler
    {
        public void doit()
        {
            S3ObjectSource bucket = getStore(requestInfo.getBucket());
            if(bucket == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            } else {
                StringBuilder rows = new StringBuilder();
                for(String key : bucket.getKeys()) {
                    rows.append(String.format("<li><a href=\"%s/%s/%s\">%s</a>%n",
                        urlprefix, bucket.getName(), key, key));
                }
                String html = String.format(OBJECTLISTHTML,
                    bucket.getName(), rows.toString());
                response.setStatus(HttpServletResponse.SC_OK);
                writeResponse(html.toGetBytes(UTF8));
            }
        }
    }

    protected static class S3BucketLister extends BaseHandler
    {
        public void doit()
        {
            StringBuilder rows = new StringBuilder();
            for(String key : bucketSource.getBucketNames()) {
                rows.append(String.format("<li><a href=\"%s%s\">%s</a>",
                    urlprefix, key, key));
            }
            String html = String.format(BUCKETLISTHTML, rows.toString());
            response.setStatus(HttpServletResponse.SC_OK);
            writeResponse(response, html.getBytes(UTF8));
        }
    }

    protected static class S3BucketChecker extends BaseHandler
    {
        public void doit()
        {
        }
    }

    protected static class S3BucketDeleter extends BaseHandler
    {
        public void doit()
        {
            if(bucketSource.deleteBucket(requestInfo.getBucket())) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }

    protected static class S3ObjectDeleter extends BaseHandler
    {
        public void doit()
        {
            S3ObjectSource store = getStore(requestInfo.getBucket());
            if(store == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            } else if(store.removeObject(requestInfo.getKey())) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }

    protected static final BaseHandler INVALID_METHOD = new BaseHandler()
    {
        public void doit()
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    };

    protected static Pattern rangepattern = Pattern.compile("bytes=([0-9]+)?-([0-9]+)?");

    static class Range
    {
        long start;
        long count;
    }

    static Range parseRange(String range, long length)
    {
        Matcher m = rangepattern.matcher(range);
        boolean b = m.matches();
        if(!b) return null;
        String startbyte = m.group(1);
        String endbyte = m.group(2);
        if(startbyte == null || startbyte.length() == 0)
            startbyte = "0";
        if(endbyte == null || startbyte.length() == 0)
            endbyte = String.format("%d", length);
        long startindex;
        long endindex;
        try {
            startindex = Long.parseLong(startbyte);
            endindex = Long.parseLong(endbyte);
        } catch (NumberFormatException nfe) {
            return null;
        }
        if(startindex < 0 || endindex < 0)
            return null;
        Range r = new Range();
        r.start = startindex;
        r.count = (endindex - startindex) + 1;
        return r;
    }


    static final String BUCKETLISTHTML =
        "<html>%n"
            + "<head>%n"
            + "<title>MockS3 Buckets</title>%n"
            + "<meta http-equiv=\"Content-Type\" content=\"text/html\">%n"
            + "</meta>%n"
            + "<body bgcolor=\"#FFFFFF\">%n"
            + "<h1>MockS3 Buckets</h1>%n"
            + "<h2>http://localhost:8080/</h2>%n"
            + "<hr>%n"
            + "<ul>%n"
            + "%s%n"
            + "</ul>%n"
            + "<hr>%n"
            + "</html>%n";

    static final String OBJECTLISTHTML =
        "<html>%n"
            + "<head>%n"
            + "<title>MockS3 Objects</title>%n"
            + "<meta http-equiv=\"Content-Type\" content=\"text/html\">%n"
            + "</meta>%n"
            + "<body bgcolor=\"#FFFFFF\">%n"
            + "<h1>MockS3 Objects</h1>%n"
            + "<h2>http://localhost:8080/%s</h2>%n"
            + "<hr>%n"
            + "<ul>%n"
            + "%s%n"
            + "</ul>%n"
            + "<hr>%n"
            + "</html>%n";
}
