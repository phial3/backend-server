package org.example.demo.pixiu.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import org.example.demo.pixiu.base.DataConstant;
import org.example.demo.pixiu.base.RestResponse;
import org.example.demo.pixiu.service.AbstractService;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @description:
 * @project: springboot-graalVM
 * @author: gaoyanfei3
 * @datetime: 2021/12/27 18:50 Monday
 */
@ControllerAdvice
public abstract class BaseController<T extends Model<?>> {

    public BaseController() {
    }

    @ResponseBody
    @ExceptionHandler({Throwable.class})
    private Object handleException(Throwable e, HttpServletRequest request) {
        return e.getMessage();
    }

    @GetMapping("{id}")
    public Object get(@PathVariable long id) {
        return service().getById(id);
    }

    @PostMapping("delete")
    public Object delete(@RequestBody Long[] ids) {
        Assert.isTrue(ids != null && ids.length > 0, "数据ID错误");
        return RestResponse.ok();
    }

    @PostMapping
    public Object save(@RequestBody T bean) {
        return RestResponse.ok().add(DataConstant.RESP_KEY_DATA, service().save(bean));
    }

    @PostMapping("update")
    public Object update(@RequestBody T bean) {
        return RestResponse.ok().add(DataConstant.RESP_KEY_DATA, service().update(new UpdateWrapper<>(bean)));
    }

    protected abstract AbstractService<T> service();
}
