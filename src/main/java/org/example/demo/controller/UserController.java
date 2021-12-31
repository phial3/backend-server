package org.example.demo.controller;

import org.apache.commons.lang3.StringUtils;
import org.example.demo.base.*;
import org.example.demo.business.UserBusiness;
import org.example.demo.entity.User;
import org.example.demo.utils.JsonUtils;
import org.phial.mybatisx.api.query.SortDirection;
import org.phial.rest.web.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * @description:
 * @project:
 * @author: gaoyanfei3
 * @datetime: 2021/12/27 16:38 Monday
 */
@Login
@RestController
@RequestMapping("api/user")
@PrivilegedMeta(@MetaProperty(name = "module", value = "用户"))
public class UserController extends DataController<User> {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final UserBusiness business;

    public UserController(UserBusiness userBusiness) {
        this.business = userBusiness;
    }

    @Override
    protected AbstractBusiness<User> business() {
        return business;
    }

    @Override
    @GetMapping("{id}")
    public Object get(@PathVariable long id) {
        LOG.info("#### UserController get() id={}", id);
        return super.get(id);
    }

    @Override
    @PostMapping("delete")
    public Object delete(@RequestBody Long[] ids) {
        LOG.info("#### UserController delete() ids={}", Arrays.asList(ids));
        return super.delete(ids);
    }

    @Override
    @PostMapping
    public Object save(@RequestBody User bean) {
        LOG.info("#### UserController save() param={}", JsonUtils.toJson(bean));
        Assert.isTrue(StringUtils.isNotBlank(bean.getUsername()), "username must not null!");
        return super.save(bean);
    }

    @Override
    @PostMapping("update")
    public Object update(@RequestBody User bean) {
        LOG.info("#### UserController update() param={}", JsonUtils.toJson(bean));
        return super.update(bean);
    }

    @GetMapping
    public Object list(@RequestParam(required = false, defaultValue = "1") Integer pageNo,
                       @RequestParam(required = false, defaultValue = "20") Integer pageSize,
                       @RequestParam(required = false, defaultValue = "id") String orderField,
                       @RequestParam(required = false, defaultValue = "DESC") SortDirection orderDirection,
                       @RequestParam(required = false) Long id,
                       @RequestParam(required = false) String name) {

        LOG.info("#### UserController list() pageNo={}, pageSize={}, orderField={}, sort={}, id={}, name={}",
                pageNo, pageSize, orderField, orderDirection, id, name);

        ParametersBuilder parametersBuilder = ParametersBuilder.custom(orderField, orderDirection);
        parametersBuilder.add("id", id).add("name", name);
        return RestResponse.ok().add(DataConstant.RESP_KEY_LIST, business().list(parametersBuilder, pageNo, pageSize))
                .add(DataConstant.RESP_KEY_TOTAL, business().count(parametersBuilder));
    }
}
