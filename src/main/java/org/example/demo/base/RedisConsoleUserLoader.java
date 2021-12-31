package org.example.demo.base;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.demo.entity.User;
import org.phial.mybatisx.starter.cache.CacheClient;
import org.phial.mybatisx.starter.cache.CacheKey;
import org.phial.rest.web.session.SessionUser;

/**
 * 用户加载器的 Redis 实现
 * @since 2020/12/9
 * @author mayanjun
 */
public class RedisConsoleUserLoader extends ConsoleUserLoader {

    private CacheClient client;

    private TypeReference<SessionUser<User>> typeReference;

    public RedisConsoleUserLoader(ConfigurableSession<User> session, CacheClient cacheClient) {
        super(session);
        this.typeReference = new TypeReference<SessionUser<User>>() {};
        this.client = cacheClient;
    }


    @Override
    public void setUserCache(SessionUser<User> user) {
        client.set(CacheKey.SYSTEM, user.getUsername(), user);
    }

    @Override
    public void removeUserCache(SessionUser<User> user) {
        client.delete(CacheKey.SYSTEM, user.getUsername());
    }

    @Override
    public SessionUser<User> getUserFromCache(String username) {
        return client.get(CacheKey.SYSTEM, username, typeReference);
    }
}
