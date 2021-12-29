package org.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import org.example.demo.base.NamedEntity;

import java.io.Serial;

/**
 * @description:
 * @project: backend-sever
 * @datetime: 2021/12/27 17:50 Monday
 */
@TableName(value = "t_user")
public final class User extends NamedEntity {

    @Serial
    private static final long serialVersionUID = -2335190050160138088L;

    public User() {
    }

    public User(Long id) {
        super(id);
    }

}
