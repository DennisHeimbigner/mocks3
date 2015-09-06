package com.snipreel.mocks3;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class MethodLoggingFilter implements Filter {

    private static final Logger log = Logger.getLogger(MockS3Servlet.class.getName());

    public void destroy() { }

    public void doFilter(
        ServletRequest req, ServletResponse rsp, FilterChain chain
    ) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest)req;
        log.info("Calling " + httpRequest.getMethod() + " for " + httpRequest.getRemoteHost() + " / " + httpRequest.getRequestURI());
        chain.doFilter(req, rsp);
    }

    public void init (FilterConfig config) { }

}
