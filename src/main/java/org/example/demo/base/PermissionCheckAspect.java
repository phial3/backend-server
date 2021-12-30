package org.example.demo.base;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.demo.ApplicationDataInitializer;
import org.example.demo.business.PrivilegeBusiness;
import org.example.demo.entity.User;
import org.phial.mybatisx.common.Assert;
import org.phial.mybatisx.common.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 权限检查拦截器
 *
 * @author mayanjun
 * @vendor mayanjun.org
 * @generator consolegen 1.0
 * @manufacturer https://mayanjun.org
 * @since 2019-10-10
 */
@Aspect
@Component
@Order(10000)
public class PermissionCheckAspect extends CachedAspect<Privileged> {

    private static final Logger LOG = LoggerFactory.getLogger(PermissionCheckAspect.class);

    private SessionManager sessionManager;

    public PermissionCheckAspect(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Pointcut("@annotation(org.example.demo.base.Privileged)")
    public void checkPermissionPointCut() {
    }

    @Around("checkPermissionPointCut()")
    public Object checkPermission(ProceedingJoinPoint jp) throws Throwable {
        // check permission
        User user = sessionManager.getCurrentUser().getOriginUser();
        MethodSignature mSig = (MethodSignature) jp.getSignature();

        Class<?> cls = jp.getTarget().getClass();
        Method method = mSig.getMethod();

        String methodName = CommonUtils.getReferenceMethodName(cls, method);
        String pName = getPermissionName(methodName);
        LOG.info("checkPermission,methodName={},privileges={}", methodName, user.getPrivileges());
        boolean ok = user.getPrivileges().contains(methodName);
        Assert.isTrue(ok, StatusCode.PERMISSION_DENIED, "当前用户缺少" + pName + "权限");
        return jp.proceed();
    }

    private String getPermissionName(String methodName) {
        Map<String, ApplicationDataInitializer.PrivilegeMetaData> map = ApplicationDataInitializer.privilegeMetaDataMap();
        ApplicationDataInitializer.PrivilegeMetaData ap = map.get(methodName);
        if (ap == null) {
            return "";
        } else {
            return "「" + ap.getName() + "」";
        }
    }

}
