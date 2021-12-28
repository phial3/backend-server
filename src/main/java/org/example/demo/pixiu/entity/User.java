package org.example.demo.pixiu.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;

/**
 * @description:
 * @project: springboot-graalVM
 * @datetime: 2021/12/27 17:50 Monday
 */
@TableName(value = "t_user")
public final class User extends NamedEntity<User> {

    @Serial
    private static final long serialVersionUID = -2335190050160138088L;

    public User() {
    }
}
