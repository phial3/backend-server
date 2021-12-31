package org.example.demo.controller;

import org.example.demo.base.*;
import org.example.demo.business.AttributeBusiness;
import org.example.demo.entity.SysSettings;
import org.example.demo.entity.User;
import org.phial.rest.web.BaseController;
import org.phial.rest.web.RestResponse;
import org.phial.rest.web.session.SessionUser;
import org.springframework.web.bind.annotation.*;

/**
 * 系统设置接口
 * @author gaoyanfei3
 */
@Login
@RequestMapping("api/settings")
@RestController
@PrivilegedMeta(@MetaProperty(name = "module", value = "系统设置"))
public class SettingsController extends BaseController {

    private AttributeBusiness business;
    private SessionManager sessionManager;

    public SettingsController(AttributeBusiness business, SessionManager sessionManager) {
        this.business = business;
        this.sessionManager = sessionManager;
    }

    @Profiler
    @GetMapping("{id}")
    @Privileged("获取{module}详细数据")
    public Object get(@PathVariable long id) {
        SessionUser<User> user = sessionManager.getCurrentUser();
        return RestResponse.ok().add(DataConstant.RESP_KEY_ENTITY, business.allSettings(user.getUsername()));
    }

    @Profiler
    @Privileged("更新{module}")
    @PostMapping("update")
    public Object update(@RequestBody SysSettings bean) {
        business.updateSettings(bean);
        return RestResponse.ok();
    }

    @Profiler
    @Privileged("恢复出厂设置")
    @PostMapping("factory")
    public Object restoreFactorySettings() {
        business.restoreFactorySettings();
        return RestResponse.ok();
    }
}
