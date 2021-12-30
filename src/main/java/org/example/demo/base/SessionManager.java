package org.example.demo.base;

import org.example.demo.config.AppConfig;
import org.example.demo.entity.User;
import org.phial.mybatisx.common.utils.BouncyCastleCrypto;
import org.phial.rest.web.session.SessionUser;
import org.phial.rest.web.session.UserLoader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

@Component
public class SessionManager extends ConfigurableSession<User> implements InitializingBean {

    public SessionManager() {
    }

    @Override
    protected UserLoader<User> createUserLoader() {
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

    @Override
    public SessionUser<User> signIn(String username, String password, HttpServletResponse response) {
        SessionUser<User> sessionUser = super.signIn(username, password, response);
        sessionUser.setLastLoginTime(System.currentTimeMillis());
        return sessionUser;
    }
}
