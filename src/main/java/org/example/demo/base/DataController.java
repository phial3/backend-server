package org.example.demo.base;

import org.example.demo.entity.User;
import org.phial.mybatisx.api.entity.Entity;
import org.phial.mybatisx.api.entity.NamedEntity;
import org.phial.mybatisx.common.utils.ClassUtils;
import org.phial.rest.common.util.Strings;
import org.phial.rest.web.BaseController;
import org.phial.rest.web.RestResponse;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;


/**
 * @description:
 * @project: backend-sever
 * @author: gaoyanfei3
 * @datetime: 2021/12/27 18:50 Monday
 */
public abstract class DataController<T extends Entity> extends BaseController {

    public DataController() {
    }

    private Class<T> beanType = null;

    /**
     * 获取实际参数类型
     *
     * @return
     */
    protected Class<T> entityType() {
        if (this.beanType != null) return beanType;
        beanType = (Class<T>) ClassUtils.getFirstParameterizedType(this.getClass());
        return beanType;
    }

    protected abstract AbstractBusiness<T> business();

    public User getUser() {
        return business().getCurrentUser();
    }

    @Profiler
    @GetMapping("{id}")
    @Privileged("获取{module}详细数据")
    public Object get(@PathVariable long id) {
        return RestResponse.ok().add(DataConstant.RESP_KEY_ENTITY, business().get(id));
    }

    @Profiler
    @Privileged("删除{module}")
    @PostMapping("delete")
    public Object delete(@RequestBody Long[] ids) {
        Assert.isTrue(ids != null && ids.length > 0, "数据ID错误");
        business().delete(ids);
        return RestResponse.ok();
    }

    @Profiler
    @Privileged("创建{module}")
    @PostMapping
    public Object save(@RequestBody T bean) {
        T ent = business().save(bean);
        return RestResponse.ok().add(DataConstant.RESP_KEY_ENTITY, ent);
    }

    @Profiler
    @Privileged("更新{module}")
    @PostMapping("update")
    public Object update(@RequestBody T bean) {
        business().update(bean);
        return RestResponse.ok();
    }

    protected Object list(ParametersBuilder pb, Integer page, Integer pageSize) {
        return RestResponse.ok()
                .add(DataConstant.RESP_KEY_LIST, business().list(pb, page, pageSize))
                .add(DataConstant.RESP_KEY_TOTAL, business().count(pb));
    }

    protected void fillNamedEntityParameters(ParametersBuilder pb, String name) {
        if (NamedEntity.class.isAssignableFrom(entityType())) {
            String pinyin = Strings.pinyin(name);
            pb.add("__LIKE__pinyin", pinyin);
        } else {
            pb.add("__LIKE__name", name);
        }
    }
}
