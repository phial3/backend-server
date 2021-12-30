package org.example.demo.base;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.demo.entity.MethodMapping;
import org.example.demo.entity.User;
import org.phial.mybatisx.api.query.Query;
import org.phial.mybatisx.api.query.QueryBuilder;
import org.phial.mybatisx.common.utils.NetUtils;
import org.phial.mybatisx.dal.dao.BasicDAO;
import org.phial.mybatisx.dal.util.JSON;
import org.phial.rest.web.session.SessionUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 日志记录器处理器
 *
 * @author mayanjun
 * @since 2019-10-10
 */
@Order
@Aspect
@Component
public class ProfilerHandlerAspect extends CachedAspect<Profiler> {

    private static final Logger LOG = LoggerFactory.getLogger(ProfilerHandlerAspect.class);

    private SessionManager sessionManager;

    private BasicDAO dao;

    private Map<Method, MethodMapping> methodCache = new ConcurrentHashMap<>();

    private ThreadPoolTaskExecutor executor;

    private Object lock = new Object();

    public ProfilerHandlerAspect(SessionManager sessionManager, BasicDAO dao, ThreadPoolTaskExecutor executor) {
        this.sessionManager = sessionManager;
        this.dao = dao;
        this.executor = executor;
    }

    @Pointcut("@annotation(org.example.demo.base.Profiler)")
    public void pointcut() {
    }

    private MethodMapping getOrSaveMapping(Method method) {
        return methodCache.computeIfAbsent(method, m -> {
            String methodName = m.toString();
            Query<MethodMapping> query = QueryBuilder.custom(MethodMapping.class)
                    .andEquivalent(MethodMapping::getName, methodName)
                    .build();
            MethodMapping mapping = dao.queryOne(query);
            if (mapping == null) {

                synchronized (lock) {
                    mapping = dao.queryOne(query);
                    if (mapping == null) {

                        mapping = new MethodMapping();
                        mapping.setName(m.toString());
                        mapping.setClassName(method.getDeclaringClass().getCanonicalName());

                        try {
                            dao.save(mapping);
                        } catch (Exception e) {
                            LOG.warn("Can't save method: method=" + methodName, e);
                        }
                    }
                }
            }
            return mapping;
        });
    }

    @Around("pointcut()")
    public Object profiler(ProceedingJoinPoint jp) throws Throwable {
        MethodSignature msig = (MethodSignature) jp.getSignature();
        Method method = msig.getMethod();
        Profiler profiler = annotation(method);
        if (profiler.ignore()) return jp.proceed();

        String profileName = profiler.value();
        String remoteIp = NetUtils.guessServerIp();

        LOG.info("remoteIp={} profileName={} methodId={}", profileName, remoteIp, getOrSaveMapping(method).getId());

        RequestAttributes ra = RequestContextHolder.currentRequestAttributes();
        if (ra instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes) ra).getRequest();
            String userAgent = request.getHeader("User-Agent");
            String remoteAddr = request.getRemoteAddr();
            if (NetUtils.isLocal(remoteAddr)) {
                remoteAddr = request.getHeader("X-Forwarded-For");
            }
            LOG.info("request contentType={} requestType={} uri={} userAgent={} remoteAddr={}",
                    request.getContentType(), request.getMethod(), request.getRequestURI(), userAgent, remoteAddr);
        }

        try {
            SessionUser<User> user = sessionManager.getCurrentUser();
            // log.setUser(user.getUsername());
        } catch (Exception e) {
        }

        if (profiler.serializeArguments()) {
            Object[] args = jp.getArgs();
            String json = serializeArguments(args);
            // log.setParameters(json);
        } else {
            // log.setParameters("ARGUMENTS IGNORED");
        }

        try {
            return jp.proceed();
        } catch (Throwable e) {
            // log.setException(true);
            String m = e.getMessage();
            if (m != null && m.length() > 200) {
                m = m.substring(0, 200);
            }
            // log.setMessage(m);
            throw e;
        } finally {
            try {
                // if (!log.getException()) {
                //      log.setMessage("SUCCESS");
                //  }
                // log.setElapsed(System.currentTimeMillis() - now);
                // async save log
                // executor.submit(() -> dao.save(log));
            } catch (Exception e) {
                LOG.error("Save access log error", e);
            }
        }
    }

    private String serializeArguments(Object args[]) {
        if (args == null) return "";
        if (args.length == 0) return "[]";

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < args.length; i++) {
            sb.append(serialize(args[i]));
            if (i < args.length - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private String serialize(Object o) {
        if (o instanceof MultipartFile) {
            MultipartFile mf = (MultipartFile) o;
            return "\"" + mf.getContentType() + ":" + mf.getSize() + ":" + mf.getName() + ":" + mf.getOriginalFilename() + "\"";
        } else if (o instanceof ServletRequest) {
            return "\"" + o.getClass().getCanonicalName() + "\"";
        } else if (o instanceof ServletResponse) {
            return "\"" + o.getClass().getCanonicalName() + "\"";
        } else {
            return JSON.se(o);
        }
    }

}
