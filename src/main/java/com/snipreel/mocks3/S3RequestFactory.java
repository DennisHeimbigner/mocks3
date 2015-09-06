package com.snipreel.mocks3;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

final class S3RequestFactory {
    
    private static final Logger log = Logger.getLogger(S3RequestFactory.class.getName());

    S3RequestFactory (S3BucketSource bucketSource) {
        this.bucketSource = bucketSource;
    }
    private final S3BucketSource bucketSource;
    
    S3RequestHandler getHandler (HttpServletRequest req, HttpServletResponse rsp) {
        S3Info info = new S3Info(req);
        BaseHandler handler = getBaseHandler(req.getMethod(), info);
        handler.bucketSource = this.bucketSource;
        handler.requestInfo = info;
        handler.response = rsp;
        return handler;
    }
    
    BaseHandler getBaseHandler (String reqMethod,S3Info info) {
        if ( reqMethod.equalsIgnoreCase("get") ) {
            if ( info.getKey().length() > 0 ) {
                return new S3ObjectGetter(true);
            } else if ( info.getBucket().length() > 0 ) {
                return new S3ObjectLister();
            } else {
                return new S3BucketLister();
            }
        } else if ( reqMethod.equalsIgnoreCase("head") ) {
            if ( info.getKey().length() > 0 ) {
                return new S3ObjectGetter(false);
            } else if ( info.getBucket().length() > 0 ) {
                return new S3BucketChecker();
            }
        } else if ( reqMethod.equalsIgnoreCase("post") ) {
        } else if ( reqMethod.equalsIgnoreCase("put") ) {
        } else if ( reqMethod.equalsIgnoreCase("delete") ) {
            if ( info.getKey().length() > 0 ) {
                return new S3ObjectDeleter();
            } else if ( info.getBucket().length() > 0 ) {
                return new S3BucketDeleter();
            }
        }
        return INVALID_METHOD;
    }
    
    private static void writeResponse (HttpServletResponse rsp, byte[] data) {
        try {
            OutputStream os = rsp.getOutputStream();
            os.write(data);
            os.flush();
        } catch (IOException ex) {
            log.warning("IO Problem writing to response");
        }
    }

    private static abstract class BaseHandler implements S3RequestHandler {
        protected S3Info requestInfo;
        protected HttpServletResponse response;
        protected S3BucketSource bucketSource;
        
        public S3Info getRequest () { return requestInfo; }
        protected S3ObjectSource getStore (String bucket) {
            return bucketSource.getBucket(bucket);
        }
    }

    private static class S3ObjectGetter extends BaseHandler {
        S3ObjectGetter (boolean includeBody) { 
            this.includeObjectBody = includeBody;
        }
        private final boolean includeObjectBody;
        public void doit () {
            S3ObjectSource store = getStore(requestInfo.getBucket());
            if ( store == null ) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            } else {
                byte[] data = store.getObject(requestInfo.getKey());
                if ( data != null ) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    if ( includeObjectBody ) writeResponse(response, data);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            }
        }
    }
    
    private static class S3ObjectLister extends BaseHandler {
        public void doit () {
            S3ObjectSource store = getStore(requestInfo.getBucket());
            if ( store == null ) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                for (String key : store.getKeys() ) {
                    
                }
            }
        }
    }
    private static class S3BucketLister extends BaseHandler {
        public void doit () {
            response.setStatus(HttpServletResponse.SC_OK);
            for (String key : bucketSource.getBucketNames() ) {
                
            }
        }
    }
    private static class S3BucketChecker extends BaseHandler {
        public void doit () {
        }
    }
    private static class S3BucketDeleter extends BaseHandler {
        public void doit () {
            if ( bucketSource.deleteBucket(requestInfo.getBucket()) ) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }
    private static class S3ObjectDeleter extends BaseHandler {
        public void doit () {
            S3ObjectSource store = getStore(requestInfo.getBucket());
            if ( store == null ) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            } else if ( store.removeObject(requestInfo.getKey()) ) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }
    
    private static final BaseHandler INVALID_METHOD = new BaseHandler () {
        public void doit () {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    };
    

}
