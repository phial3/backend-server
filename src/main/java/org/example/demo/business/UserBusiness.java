package org.example.demo.business;

import org.apache.commons.lang3.StringUtils;
import org.example.demo.base.AbstractBusiness;
import org.example.demo.entity.User;
import org.phial.mybatisx.api.query.QueryBuilder;
import org.phial.mybatisx.common.Assert;
import org.phial.mybatisx.common.utils.RegexUtils;
import org.phial.rest.common.util.Strings;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @project: backend-sever
 * @author: gaoyanfei3
 * @datetime: 2021/12/27 18:44 Monday
 */
@Service
@DependsOn("ApplicationDataInitializer")
public class UserBusiness extends AbstractBusiness<User> {
    private static final String ADMIN_USERNAME = "admin";

    private final AttributeBusiness attributeBusiness;

    public UserBusiness(AttributeBusiness attributeBusiness) {
        this.attributeBusiness = attributeBusiness;
    }

    public  Map<String, String> settings() {
        // 加载用户属性
        return attributeBusiness.allSettings(getCurrentUser().getUsername());
    }

    public String secretKey(Long id) {
        Assert.greatThanZero(id, "用户ID错误");
        User user = service.getInclude(new User(id), "secretKey");
        Assert.notNull(user, "用户不存在");
        return user.getSecretKey();
    }

    @Override
    protected void doCheck(User entity, boolean update) {
        if (update) {
            entity.setSecretKey(null);

        } else {
            Assert.notBlank(entity.getUsername(), "username must not null!");
            Assert.isTrue(RegexUtils.checkName(entity.getUsername()), "username is illegal !");
            List<User> records = dao().query(
                    QueryBuilder.custom(User.class)
                            .andEquivalent(User::getUsername, entity.getUsername())
                            .build()
            );
            Assert.isTrue(records.isEmpty(), "username has exists!");

            if (!ADMIN_USERNAME.equalsIgnoreCase(entity.getUsername())) {
                entity.setAdministrator(false);
            }

            if (StringUtils.isBlank(entity.getPassword())) {
                entity.setPassword("123456");
            }
            entity.setSecretKey(Strings.secretKey(32));
            String enc = sessionManager.encryptPassword(entity.getPassword());
            entity.setPassword(enc);
        }
    }
}
