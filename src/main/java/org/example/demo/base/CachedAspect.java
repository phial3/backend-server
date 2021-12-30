package org.example.demo.base;

import org.phial.mybatisx.common.utils.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class CachedAspect<T extends Annotation> {

    private Map<Method, T> cache = new HashMap<>();

    private Class<T> annotationType;

    protected T annotation(Method method) {
        return cache.computeIfAbsent(method, m -> m.getAnnotation(annotationType()));
    }

    protected Class<T> annotationType() {
        if (this.annotationType != null) return annotationType;
        annotationType = (Class<T>) ClassUtils.getFirstParameterizedType(this.getClass());
        return annotationType;
    }

}
