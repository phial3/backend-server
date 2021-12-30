package org.example.demo.base;

import org.example.demo.entity.User;
import org.phial.myrest.session.UserLoader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class SessionManager extends ConfigurableSession<User> implements InitializingBean {

    public SessionManager() {
    }

    @Override
    protected void configSession(UserLoader<User> userLoader) {
        setUserLoader(userLoader);
    }

    @Override
    protected UserLoader<User> createUserLoader() {
        return getUserLoader();
    }
}
