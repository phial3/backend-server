package org.example.demo.base;

import org.example.demo.config.AppConfig;
import org.example.demo.entity.User;
import org.phial.mybatisx.common.utils.BouncyCastleCrypto;
import org.phial.mybatisx.starter.cache.CacheClient;
import org.phial.rest.web.session.UserLoader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class SessionManager extends ConfigurableSession<User> implements InitializingBean {
    @Resource
    private CacheClient cacheClient;

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
}
