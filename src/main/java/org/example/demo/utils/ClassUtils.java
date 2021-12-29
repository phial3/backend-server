package org.example.demo.utils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @project: backend-server
 * @author: gaoyanfei3
 * @datetime: 2021/12/29 15:27 Wednesday
 */
public class ClassUtils {
    private static final Map<Class<?>, Map<String, Field>> FIELDS_CACHE = new ConcurrentHashMap(new IdentityHashMap());

    private ClassUtils() {
    }

    public static Collection<Field> getAllFields(Class<?> cls) {
        return getAllFieldMap(cls).values();
    }

    public static Map<String, Field> getAllFieldMap(Class<?> cls) {
        Map<String, Field> fieldMap = (Map) FIELDS_CACHE.get(cls);
        if (fieldMap == null) {
            fieldMap = getAllInheritedFields(cls);
            FIELDS_CACHE.put(cls, fieldMap);
        }

        return fieldMap;
    }

    private static Map<String, Field> getAllInheritedFields(Class<?> cls) {
        Map<String, Field> map = new HashMap();
        if (cls == Object.class) {
            return map;
        } else {
            Map<String, Field> superMap = getAllInheritedFields(cls.getSuperclass());
            if (!superMap.isEmpty()) {
                map.putAll(superMap);
            }

            Field[] fields = cls.getDeclaredFields();
            if (fields != null && fields.length > 0) {
                Field[] var4 = fields;
                int var5 = fields.length;

                for (int var6 = 0; var6 < var5; ++var6) {
                    Field f = var4[var6];
                    map.put(f.getName(), f);
                }
            }
            return map;
        }
    }

    public static Field getField(Class<?> cls, String name) {
        Map<String, Field> fieldMap = getAllFieldMap(cls);
        return (Field) fieldMap.get(name);
    }

    public static Class<?> getFirstParameterizedType(Class<?> beanType) {
        Type t = beanType.getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType) t;
        Type[] ats = pt.getActualTypeArguments();
        return (Class) ats[0];
    }
}
