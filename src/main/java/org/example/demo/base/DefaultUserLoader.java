package org.example.demo.base;


import org.phial.mybatisx.api.query.QueryBuilder;
import org.phial.mybatisx.common.Assert;
import org.phial.myrest.session.AbstractSession;
import org.phial.myrest.session.SessionUser;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultUserLoader<T extends AbstractUser> extends AbstractUserLoader<T> {

    private Map<String, SessionUser<T>> userCache = new ConcurrentHashMap<>();

    public DefaultUserLoader(ConfigurableSession<T> session) {
        super(session);
    }

    @Override
    public SessionUser<T> loadUser(String username) {
        Assert.notBlank(username, "用户名不能为空");
        T user = queryUser(username);
        SessionUser<T> sessionUser = new SessionUser<>(user.getUsername());
        sessionUser.setId(user.getId());
        sessionUser.setPassword(user.getPassword());
        sessionUser.setOriginUser(user);
        sessionUser.setLastLoginTime(System.currentTimeMillis());
        return sessionUser;
    }

    /**
     * 查询用户
     * @param username
     * @return
     */
    protected T queryUser(String username) {
        QueryBuilder<T> query = QueryBuilder.custom(session().userType())
                .andEquivalent(T::getUsername, username)
                .andEquivalent(T::getEnabled, true);

        T user = session().dao().queryOne(query.build());
        Assert.notNull(user, AbstractSession.USERNAME_OR_PASSWORD_INCORRECT);
        return user;
    }

    @Override
    public void setUserCache(SessionUser<T> user) {
        userCache.put(user.getUsername(), user);
    }

    @Override
    public void removeUserCache(SessionUser<T> user) {
        userCache.remove(user.getUsername());
    }

    @Override
    public SessionUser<T> getUserFromCache(String username) {
        return userCache.get(username);
    }

}
