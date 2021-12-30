package org.example.demo.base;


import org.phial.mybatisx.common.ServiceException;
import org.phial.mybatisx.common.utils.ClassUtils;
import org.phial.mybatisx.dal.dao.BasicDAO;
import org.phial.myrest.session.AESSession;
import org.phial.myrest.session.SessionUser;
import org.phial.myrest.session.UserLoader;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;

public abstract class ConfigurableSession<T extends AbstractUser> extends AESSession<T> implements InitializingBean {

    protected ThreadLocal<String> systemThreadLocal = new ThreadLocal<>();

    @Resource
    private BasicDAO dao;

    private UserLoader<T> userLoader;

    @Override
    public void afterPropertiesSet() throws Exception {
        configSession(userLoader());
    }

    public BasicDAO dao() {
        return dao;
    }

    protected abstract void configSession(UserLoader<T> userLoader);

    /**
     * 返回用户加载器的实现类，由子类实现
     *
     * @return
     */
    public UserLoader<T> userLoader() {
        if (userLoader == null) {
            synchronized (this) {
                UserLoader<T> ul = userLoader;
                if (ul == null) {
                    ul = createUserLoader();
                }
                userLoader = ul;
            }
        }
        return userLoader;
    }


    protected abstract UserLoader<T> createUserLoader();


    private Class<T> userType;

    public Class<T> userType() {
        if (this.userType != null) {
            return userType;
        }
        userType = (Class<T>) ClassUtils.getFirstParameterizedType(this.getClass());
        return userType;
    }

    public void updateLastLoginTime(String username) {
        SessionUser<T> user = getUserLoader().getUserFromCache(username);
        if (user != null) {
            user.setLastLoginTime(System.currentTimeMillis());
        }
    }

    @Override
    public String decryptToken(String token) {
        try {
            return super.decryptToken(token);
        } catch (Exception e) {
            throw new ServiceException(NO_SIGN_IN);
        }
    }

    @Override
    protected Cookie createSigninCookie(String token) {
        Cookie c = super.createSigninCookie(token);
        c.setHttpOnly(true);
        c.setMaxAge(-1);
        return c;
    }

    @Override
    public void clear() {
        super.clear();
        systemThreadLocal.remove();
    }
}
