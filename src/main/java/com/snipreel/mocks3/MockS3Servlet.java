package com.snipreel.mocks3;

import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockS3Servlet extends HttpServlet {
    private static final long serialVersionUID = 0L;

    private static final Logger log = Logger.getLogger(MockS3Servlet.class.getName());
    
    private S3RequestFactory requestFactory;
    
    @Override
    public void init () {
        String storeType = getServletConfig().getInitParameter(S3ObjectSource.class.getName());
        S3BucketSource storeSource = S3ObjectsSource.getInstance().getStoreSource(storeType);
        log.fine("Created " + storeSource + " from " + storeType);
        this.requestFactory = new S3RequestFactory(storeSource);
    }
    
    @Override
    protected void doGet (HttpServletRequest req, HttpServletResponse rsp) {
        requestFactory.getHandler(req, rsp).doit();
    }
    
    @Override
    protected void doHead (HttpServletRequest req, HttpServletResponse rsp) {
        requestFactory.getHandler(req, rsp).doit();
    }

    @Override
    protected void doPost (HttpServletRequest req, HttpServletResponse rsp) {
        requestFactory.getHandler(req, rsp).doit();
    }

    @Override
    protected void doPut (HttpServletRequest req, HttpServletResponse rsp) {
        requestFactory.getHandler(req, rsp).doit();
    }

    @Override
    protected void doDelete (HttpServletRequest req, HttpServletResponse rsp) {
        requestFactory.getHandler(req, rsp).doit();
    }
}
