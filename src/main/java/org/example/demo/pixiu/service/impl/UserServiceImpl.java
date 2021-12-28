package org.example.demo.pixiu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.demo.pixiu.entity.User;
import org.example.demo.pixiu.mapper.UserMapper;
import org.example.demo.pixiu.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @project: springboot-graalVM
 * @author: gaoyanfei3
 * @datetime: 2021/12/27 18:44 Monday
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
