package org.example.demo.base;

import org.apache.commons.lang3.StringUtils;
import org.example.demo.entity.User;
import org.phial.myrest.interceptor.AnnotationBasedHandlerInterceptor;
import org.phial.myrest.session.SessionUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequestInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录检查拦截器
 *
 * @author phial
 * @vendor phial.org
 * @generator consolegen 1.0
 * @manufacturer https://phial.org
 * @since 2019-10-10
 */
//@Component
public class LoginInterceptor extends AnnotationBasedHandlerInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(LoginInterceptor.class);

    @Resource
    private SessionManager session;

    public LoginInterceptor(WebRequestInterceptor requestInterceptor) {
        super(requestInterceptor);
    }

    @Override
    public int getOrder() {
        return InterceptorOrder.LOGIN.ordinal();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Login login = findAnnotation(Login.class, handler);

        if (login.checkUser()) {
            try {
                SessionUser<User> user = session.getUser(request);
                if (user == null) return false;
                if (StringUtils.isNotBlank(user.getDescription())) {
                    request.setAttribute("__current_user", user.getUsername() + "(" + user.getDescription() + ")");
                } else {
                    request.setAttribute("__current_user", user.getUsername());
                }
                session.updateLastLoginTime(user.getUsername());
                return true;
            } catch (Exception e) {
                LOG.error("Unknown exception on login, uri={}, message={}", request.getRequestURI(), e.getMessage());
                String uri = request.getRequestURI();
                if (uri.startsWith("/api")) throw e;
                response.sendRedirect("/login");
                return false;
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        session.clear();
    }
}