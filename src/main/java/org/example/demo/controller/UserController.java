package org.example.demo.controller;

import org.example.demo.base.*;
import org.example.demo.entity.User;
import org.example.demo.service.RoleService;
import org.example.demo.service.UserService;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @description:
 * @project: java-web-graalVM
 * @author: gaoyanfei3
 * @datetime: 2021/12/27 16:38 Monday
 */
@RestController
@RequestMapping("api/user")
public class UserController extends BaseController<User> {

    @Resource
    private UserService userService;

    @Resource
    private RoleService roleService;

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
        Assert.isTrue(StringUtils.hasText(bean.getName()), "name is not null");
        return super.save(bean);
    }

    @Override
    @PostMapping("update")
    public Object update(@RequestBody User bean) {
        return super.update(bean);
    }

    @GetMapping
    public Object list(@RequestParam(required = false, defaultValue = "1") Integer pageNo,
                       @RequestParam(required = false, defaultValue = "20") Integer pageSize,
                       @RequestParam(required = false, defaultValue = "id") String orderField,
                       @RequestParam(required = false, defaultValue = "DESC") SortDirection orderDirection,
                       @RequestParam(required = false) Long id,
                       @RequestParam(required = false) String name) {

        ParametersBuilder<User> parametersBuilder = ParametersBuilder.<User>custom(orderField, orderDirection);
        parametersBuilder.add("id", id).add("name", name);
        return RestResponse.ok().add(DataConstant.RESP_KEY_LIST, service().list(parametersBuilder, pageNo, pageSize))
                .add(DataConstant.RESP_KEY_TOTAL, service().count(parametersBuilder));
    }
}
