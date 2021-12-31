package org.example.demo.base.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.example.demo.base.InterceptorOrder;
import org.example.demo.base.Login;
import org.example.demo.base.SessionManager;
import org.example.demo.entity.User;
import org.phial.rest.web.interceptor.AnnotationBasedHandlerInterceptor;
import org.phial.rest.web.session.SessionUser;
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
 WebRequestInterceptor的入参WebRequest是包装了HttpServletRequest 和HttpServletResponse的，通过WebRequest获取Request中的信息更简便。
 WebRequestInterceptor的preHandle是没有返回值的，说明该方法中的逻辑并不影响后续的方法执行，所以这个接口实现就是为了获取Request中的信息，或者预设一些参数供后续流程使用。
 HandlerInterceptor的功能更强大也更基础，可以在preHandle方法中就直接拒绝请求进入controller方法。
 */
@Component
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
                if (user == null) {
                    return false;
                }
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
                if (uri.startsWith("/api")) {
                    throw e;
                }

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