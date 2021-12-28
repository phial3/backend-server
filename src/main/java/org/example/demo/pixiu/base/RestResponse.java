package org.example.demo.pixiu.base;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @project: springboot-graalVM
 * @author: gaoyanfei3
 * @datetime: 2021/12/27 18:59 Monday
 */
public class RestResponse extends HashMap<String, Object> {

    public static final String CODE_KEY = "code";
    public static final String MSG_KEY = "msg";
    public static final String DESC_KEY = "desc";
    public static final String DATA_KEY = "data";

    public RestResponse() {
        this(0, "OK");
    }

    public RestResponse(int code, String message) {
        super();
        setStatus(code, message);
    }

    private void setStatus(int code, String message) {
        this.put(CODE_KEY, code);
        this.put(MSG_KEY, message);
    }

    public static RestResponse ok() {
        return new RestResponse(RestStatus.OK);
    }

    public static RestResponse ok(Object data) {
        return new RestResponse(RestStatus.OK).setData(data);
    }

    public static RestResponse error() {
        return new RestResponse(RestStatus.INTERNAL_ERROR);
    }

    public static RestResponse error(Object data) {
        return new RestResponse(RestStatus.INTERNAL_ERROR).setData(data);
    }

    public RestResponse(RestStatus status) {
        super();
        if(status != null) setStatus(status.getCode(), status.getMessage());
    }

    public RestResponse setData(Object object) {
        this.put(DATA_KEY, object);
        return this;
    }

    public Object getData() {
        return this.get(DATA_KEY);
    }

    public int getCode() {
        Object o = get(CODE_KEY);
        if (o != null && o instanceof Number) return ((Number) o).intValue();
        return 0;
    }

    public RestResponse setCode(int code) {
        this.put(CODE_KEY, code);
        return this;
    }

    public String getMessage() {
        Object msg = get(MSG_KEY);
        if(msg != null) return msg.toString();
        return null;
    }

    public RestResponse setMessage(String message) {
        this.put(MSG_KEY, message);
        return this;
    }

    public RestResponse add(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public RestResponse addAll(Map<? extends String, ?> m) {
        super.putAll(m);
        return this;
    }


    public RestResponse setDescription(String description) {
        this.put(DESC_KEY, description);
        return this;
    }

    public String getDescription() {
        Object desc = get(DESC_KEY);
        if(desc != null) return desc.toString();
        return null;
    }
}