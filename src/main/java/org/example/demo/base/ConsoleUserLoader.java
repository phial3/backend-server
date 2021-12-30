package org.example.demo.base;

import org.example.demo.entity.User;
import org.phial.mybatisx.api.query.Query;
import org.phial.mybatisx.api.query.QueryBuilder;
import org.phial.mybatisx.common.Assert;
import org.phial.mybatisx.dal.dao.BasicDAO;
import org.springframework.stereotype.Component;

@Component
public class ConsoleUserLoader extends DefaultUserLoader<User> {

    public ConsoleUserLoader(ConfigurableSession<User> session) {
        super(session);
    }

    @Override
    protected User queryUser(String username) {

        BasicDAO dao = session().dao();

        Query<User> query = QueryBuilder.custom(User.class)
                .andEquivalent(User::getUsername, username)
                .build();
        User user = dao.queryOne(query);
        Assert.notNull(user, "用户名或密码错误!");
        return user;
    }
}
