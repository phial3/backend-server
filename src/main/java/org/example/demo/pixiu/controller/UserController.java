package org.example.demo.pixiu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.demo.pixiu.base.DataConstant;
import org.example.demo.pixiu.base.Limit;
import org.example.demo.pixiu.base.RestResponse;
import org.example.demo.pixiu.base.SortDirection;
import org.example.demo.pixiu.entity.User;
import org.example.demo.pixiu.service.AbstractService;
import org.example.demo.pixiu.service.RoleService;
import org.example.demo.pixiu.service.UserService;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description:
 * @project: java-web-graalVM
 * @author: gaoyanfei3
 * @datetime: 2021/12/27 16:38 Monday
 */
@RestController
@RequestMapping("api/user")
public class UserController extends BaseController<User> {
    private UserService userService;
    private RoleService roleService;

    public UserController(UserService userService,
                          RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @Override
    protected AbstractService<User> service() {
        return userService;
    }

    @Override
    @GetMapping("{id}")
    public Object get(@PathVariable long id) {
        return super.get(id);
    }

    @Override
    @PostMapping("delete")
    public Object delete(@RequestBody Long[] ids) {
        return super.delete(ids);
    }

    @Override
    @PostMapping
    public Object save(@RequestBody User bean) {
        List<User> userList = service().list(
                new LambdaQueryWrapper<User>().eq(User::getName, bean.getName())
        );
        Assert.isTrue(userList.isEmpty(), "user name has exists!");
        return super.save(bean);
    }

    @Override
    @PostMapping("update")
    public Object update(@RequestBody User bean) {
        Assert.notNull(bean.getId(), "ID can't is null!");
        return super.update(bean);
    }

    @GetMapping
    public Object list(@RequestParam(required = false, defaultValue = "1") Integer pageNo,
                       @RequestParam(required = false, defaultValue = "20") Integer pageSize,
                       @RequestParam(required = false, defaultValue = "id") String orderField,
                       @RequestParam(required = false, defaultValue = "DESC") SortDirection orderDirection,
                       @RequestParam(required = false) Long id,
                       @RequestParam(required = false) String name) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if (id != null && id > 0) {
            wrapper.eq("id", id);
        }
        if (StringUtils.hasText(name)) {
            wrapper.eq("name", name);
        }
        wrapper.orderBy(false, SortDirection.isAsc(orderDirection), orderField)
                .last(true, "LIMIT " + Limit.of(pageNo, pageSize));

        List<User> userList = service().list(wrapper);
        long count = service().count(wrapper);
        return RestResponse.ok().add(DataConstant.RESP_KEY_LIST, userList)
                .add(DataConstant.RESP_KEY_TOTAL, count);
    }
}
