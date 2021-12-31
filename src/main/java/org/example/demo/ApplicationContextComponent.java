package org.example.demo;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @description:
 * @project: backend-server
 * @author: gaoyanfei3
 * @datetime: 2021/12/31 17:36 Friday
 */
@Component
public class ApplicationContextComponent implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext appContext) throws BeansException {
        applicationContext = appContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 根据Bean名称获取Bean对象
     *
     * @param name Bean名称
     * @return 对应名称的Bean对象
     */
    private static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    /**
     * 根据Bean的类型获取对应的Bean
     *
     * @param requiredType Bean类型
     * @return 对应类型的Bean对象
     */
    private static <T> T getBean(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }

    /**
     * 根据Bean名称获取指定类型的Bean对象
     *
     * @param name         Bean名称
     * @param requiredType Bean类型（可为空）
     * @return 获取对应Bean名称的指定类型Bean对象
     */
    private static <T> T getBean(String name, Class<T> requiredType) {
        return applicationContext.getBean(name, requiredType);
    }

    private static Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> clazz) {
        return applicationContext.getBeansWithAnnotation(clazz);
    }

    /**
     * 判断是否包含对应名称的Bean对象
     *
     * @param name Bean名称
     * @return 包含：返回true，否则返回false。
     */
    private static boolean containsBean(String name) {
        return applicationContext.containsBean(name);
    }

    /**
     * 获取对应Bean名称的类型
     *
     * @param name Bean名称
     * @return 返回对应的Bean类型
     */
    private static Class<?> getType(String name) {
        return applicationContext.getType(name);
    }
}
