package org.example.demo.base;

/**
 * 元属性，可配合注解进行属性配置
 * @since 2019-10-10
 * @author mayanjun
 */
public @interface MetaProperty {

    String name();

    String value();
}
