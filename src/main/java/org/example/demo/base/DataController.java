package org.example.demo.base;

import org.phial.mybatisx.api.entity.Entity;
import org.phial.myrest.RestResponse;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

/**
 * @description:
 * @project: backend-sever
 * @author: gaoyanfei3
 * @datetime: 2021/12/27 18:50 Monday
 */
@ControllerAdvice
public abstract class DataController<T extends Entity> {

    protected abstract AbstractBusiness<T> service();

    public DataController() {
    }

    @ResponseBody
    @ExceptionHandler({Throwable.class})
    protected Object handleException(Throwable e) {
        return e.getMessage();
    }

    @GetMapping("{id}")
    protected Object get(@PathVariable long id) {
        return service().get(id);
    }

    @PostMapping("delete")
    protected Object delete(@RequestBody Long[] ids) {
        Assert.isTrue(ids != null && ids.length > 0, "参数ID错误");
        service().delete(ids);
        return RestResponse.ok();
    }

    @PostMapping
    protected Object save(@RequestBody T bean) {
        return RestResponse.ok().add(DataConstant.RESP_KEY_DATA, service().save(bean));
    }

    @PostMapping("update")
    protected Object update(@RequestBody T bean) {
        service().update(bean);
        return RestResponse.ok();
    }

    protected Object list(ParametersBuilder pb, Integer page, Integer pageSize) {
        return RestResponse.ok()
                .add(DataConstant.RESP_KEY_LIST, service().list(pb, page, pageSize))
                .add(DataConstant.RESP_KEY_TOTAL, service().count(pb));
    }
}
