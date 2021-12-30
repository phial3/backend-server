package org.example.demo.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

/**
 * @description:
 * @project: backend-server
 * @author: gaoyanfei3
 * @datetime: 2021/12/29 12:53 Wednesday
 */
public class JsonUtils {

    public static ObjectMapper objMapper = new ObjectMapper();

    static {
        // 此配置的作用为当使用此工具将json中的属性还原到bean时，如果有bean中没有的属性，是否报错
        objMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * json 转换成 bean
     *
     * @param <T>
     * @param json
     * @param clazz
     * @return
     */
    public static <T> T parseBean(String json, Class<T> clazz) {
        if (json == null || clazz == null) {
            return null;
        }
        try {
            return objMapper.readValue(json, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * json node 转换成bean
     *
     * @param node
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T parseBean(JsonNode node, Class<T> clazz) {
        if (node == null || clazz == null) {
            return null;
        }
        try {
            return objMapper.treeToValue(node,clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * bean 转换成 json
     *
     * @param bean
     * @return
     */
    public static String toJson(Object bean) {
        if (bean == null) {
            return null;
        }
        try {
            return objMapper.writeValueAsString(bean);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JsonNode stringToJsonNode(String jsonString) {
        if (jsonString == null) {
            return null;
        }
        try {
            return objMapper.readTree(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static <T, A extends List<T>> A toList(String jsonString, TypeReference<List> clazz) throws IOException {
        return (A) objMapper.readValue(jsonString, clazz);
    }
}
