package org.example.demo.business;

import org.example.demo.base.AbstractBusiness;
import org.example.demo.entity.User;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @project: backend-sever
 * @author: gaoyanfei3
 * @datetime: 2021/12/27 18:44 Monday
 */
@Service
public class UserBusiness  extends AbstractBusiness<User> {

    @Override
    protected void doCheck(User entity, boolean update) {

    }
}
