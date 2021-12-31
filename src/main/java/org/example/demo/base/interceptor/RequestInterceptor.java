package org.example.demo.base.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

/**
 * @description:
 * @project: backend-server
 * @author: gaoyanfei3
 * @datetime: 2021/12/31 20:06 Friday
 */
@Component
public class RequestInterceptor implements WebRequestInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(RequestInterceptor.class);

    @Override
    public void preHandle(WebRequest request) throws Exception {
        LOG.info("#### RequestInterceptor preHandle() ... ");
    }

    @Override
    public void postHandle(WebRequest request, ModelMap model) throws Exception {
        LOG.info("#### RequestInterceptor postHandle() ... ");
    }

    @Override
    public void afterCompletion(WebRequest request, Exception ex) throws Exception {
        LOG.info("#### RequestInterceptor afterCompletion() ... ");
    }
}
