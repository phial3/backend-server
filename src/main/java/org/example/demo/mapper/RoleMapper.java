package org.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.demo.entity.Role;

/**
 * @description:
 * @project: springboot-graalVM
 * @author: gaoyanfei3
 * @datetime: 2021/12/27 18:47 Monday
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role>  {
}
