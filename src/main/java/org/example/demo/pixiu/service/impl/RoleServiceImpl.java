package org.example.demo.pixiu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.demo.pixiu.entity.Role;
import org.example.demo.pixiu.mapper.RoleMapper;
import org.example.demo.pixiu.service.RoleService;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @project: springboot-graalVM
 * @author: gaoyanfei3
 * @datetime: 2021/12/27 18:48 Monday
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
}
