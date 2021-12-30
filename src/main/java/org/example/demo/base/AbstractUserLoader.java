package org.example.demo.base;

import org.phial.myrest.session.UserLoader;

public abstract class AbstractUserLoader<T extends AbstractUser> implements UserLoader<T> {

    private ConfigurableSession<T> session;

    public AbstractUserLoader(ConfigurableSession<T> session) {
        this.session = session;
    }

    public ConfigurableSession<T> session() {
        return session;
    }
}
