package org.example.demo.base;

import org.example.demo.config.AppConfig;
import org.example.demo.entity.User;
import org.example.demo.utils.RedisCacheClient;
import org.phial.mybatisx.api.query.QueryBuilder;
import org.phial.mybatisx.common.Assert;
import org.phial.mybatisx.common.utils.BouncyCastleCrypto;
import org.phial.rest.web.session.SessionUser;
import org.phial.rest.web.session.UserLoader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Component
public class SessionManager extends ConfigurableSession<User> implements InitializingBean {
    @Resource
    private RedisCacheClient cacheClient;

    private ThreadLocal<SessionUser<User>> currentUser = new ThreadLocal<>();

    @Override
    protected UserLoader<User> createUserLoader() {
        if (config().isClusterEnabled()) {
            return new RedisConsoleUserLoader(this, cacheClient);
        }
        return new ConsoleUserLoader(this);
    }

    @Override
    protected void configSession(UserLoader<User> userLoader) {
        setUserLoader(userLoader);
        AppConfig config = config();
        setDomain(config.getDomain());
        setTokenName(config.getTokenCookieName());
        setCrypto(new BouncyCastleCrypto(config.getConsoleAesKey().secretKeyStore()));
    }

    /// forward should remove when prod

    @Override
    public SessionUser<User> getUser(HttpServletRequest request) {
        SessionUser<User> cacheUser = cacheClient.getUserFromCache(request.getHeader("clientId"));
        this.currentUser.set(cacheUser);
        return cacheUser;
    }

    @Override
    public SessionUser<User> getCurrentUser() {
        SessionUser<User> user = this.currentUser.get();
        Assert.notNull(user, NO_SIGN_IN);
        return user;
    }

    @Override
    public SessionUser<User> signIn(String username, String password, HttpServletResponse response) {
        return getUser(username);
    }

    public SessionUser<User> getUser(String username) {
        // from cache
        SessionUser<User> cacheUser = cacheClient.getUserFromCache(username);
        if (cacheUser == null) {
            // from db
            List<User> userRecords = dao().query(
                    QueryBuilder.custom(User.class)
                            .andEquivalent("username", username)
                            .build()
            );
            Assert.notEmpty(userRecords, "username" + username + " not exists.");
            User userRecord = userRecords.get(0);

            Assert.isTrue(userRecord.getEnabled(), StatusCode.USER_DISABLE);

            cacheUser = new SessionUser<>();
            cacheUser.setOriginUser(userRecord);
            cacheUser.setUsername(userRecord.getUsername());
            cacheUser.setPassword(userRecord.getPassword());
            cacheUser.setLastLoginTime(userRecord.getLoginTime());
            cacheUser.setId(userRecord.getId());
            cacheUser.setDescription(userRecord.getDescription());

            cacheClient.setUserCache(cacheUser);
        }
        return cacheUser;
    }
}
