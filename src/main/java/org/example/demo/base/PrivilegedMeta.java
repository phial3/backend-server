package org.example.demo.base;


import org.phial.mybatisx.api.entity.Entity;

import java.lang.annotation.*;

/**
 * 如果一个类中有 @Privileged 方法，则该方法所在的类必须使用这个注解来描述模块的信息。
 * 这个机制主要是为了描述继承下来的 @Privileged 方法。在 @Privileged 注解中可以使用 {propertyName} 来引用这里的属性
 * @since 2019-10-10
 * @author phial
 * @vendor 
 * @generator consolegen 1.0
 * @manufacturer 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface PrivilegedMeta {

    MetaProperty[] value() default {};

    Class<? extends Entity> entityType() default Entity.class;
}