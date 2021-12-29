package org.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.demo.base.AbstractService;
import org.example.demo.entity.User;
import org.example.demo.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description:
 * @project: springboot-graalVM
 * @author: gaoyanfei3
 * @datetime: 2021/12/27 18:44 Monday
 */
@Service
public class UserService extends AbstractService<User> {
    @Resource
    private UserMapper userMapper;

    @Override
    protected void doCheck(User entity, boolean update) {
        if (update) {
            Assert.notNull(entity.getId(), "id must not null.");
        } else {
            LambdaQueryWrapper<User> wrapper = new QueryWrapper<User>().lambda().eq(User::getName, entity.getName());
            List<User> userList = this.doQuery(wrapper);
            Assert.isTrue(userList.isEmpty(), "user name has exists!");
        }
    }

    @Override
    protected BaseMapper<User> mapper() {
        return userMapper;
    }

}
