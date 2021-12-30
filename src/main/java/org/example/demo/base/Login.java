package org.example.demo.base;


import org.phial.rest.web.interceptor.Interceptor;

import java.lang.annotation.*;

/**
 * 登录检查注解
 * @since 2019-10-10
 * @author phial
 * @vendor phial.org
 * @generator consolegen 1.0
 * @manufacturer https://phial.org
 */
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Interceptor(loadFromContainer = true, value = LoginInterceptor.class)
public @interface Login {

    boolean checkUser() default true;
}