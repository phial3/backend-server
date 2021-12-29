package org.example.demo.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.demo.base.AbstractService;
import org.example.demo.entity.Role;
import org.example.demo.mapper.RoleMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @description:
 * @project: backend-sever
 * @author: gaoyanfei3
 * @datetime: 2021/12/27 18:48 Monday
 */
@Service
public class RoleService extends AbstractService<Role> {

    @Resource
    private RoleMapper roleMapper;

    @Override
    protected BaseMapper<Role> mapper() {
        return roleMapper;
    }

}
