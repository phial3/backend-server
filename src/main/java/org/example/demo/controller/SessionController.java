package org.example.demo.controller;

import org.example.demo.base.DataConstant;
import org.example.demo.base.Login;
import org.example.demo.base.Profiler;
import org.example.demo.base.SessionManager;
import org.example.demo.business.UserBusiness;
import org.example.demo.config.AppConfig;
import org.example.demo.entity.User;
import org.example.demo.utils.JsonUtils;
import org.phial.mybatisx.common.ServiceException;
import org.phial.rest.web.BaseController;
import org.phial.rest.web.RestResponse;
import org.phial.rest.web.session.SessionUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @since 2019-07-10
 * @author mayanjun
 * @vendor JDD (https://www.jddglobal.com)
 */
@RestController
@RequestMapping("api/session")
public class SessionController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(SessionController.class);

    private AppConfig config;
    private SessionManager sessionManager;
    private UserBusiness userBusiness;

    public SessionController(AppConfig config, SessionManager sessionManager, UserBusiness userBusiness) {
        this.config = config;
        this.sessionManager = sessionManager;
        this.userBusiness = userBusiness;
    }

    @Profiler(serializeArguments = false)
    @RequestMapping(method = RequestMethod.POST)
    public Object sign(@RequestBody User user, HttpServletResponse response) {
        try {
            LOG.info("#### SessionController sign() params={}", JsonUtils.toJson(user));
            SessionUser<User> signResult = sessionManager.signIn(user.getUsername(), user.getPassword(), response);
            return RestResponse.ok(signResult.getOriginUser());
        } catch (ServiceException e) {
            throw new ServiceException(e.getStatus(), "用户名或者密码错误");
        }
    }

    @Login
    @Profiler
    @RequestMapping(method = RequestMethod.GET, value = "profile")
    public Object profile() {
        User user = sessionManager.getCurrentUser().getOriginUser();
        user.setPassword(null);
        return RestResponse.ok()
                .add(DataConstant.RESP_KEY_SYS_USER, user)
                .add(DataConstant.RESP_KEY_SETTINGS, userBusiness.settings());
    }

    @Login
    @Profiler
    @RequestMapping(method = RequestMethod.GET, value = "signOut")
    public Object signOut(HttpServletRequest request, HttpServletResponse response) {
        LOG.info("#### SessionController signOut() user={}", sessionManager.getCurrentUser().getUsername());
        sessionManager.signOut(request, response);
        return RestResponse.ok().add(DataConstant.RESP_KEY_DATA, config.getDomain());
    }
}
