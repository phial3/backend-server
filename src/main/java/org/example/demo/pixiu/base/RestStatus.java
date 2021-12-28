package org.example.demo.pixiu.base;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description:
 * @project: springboot-graalVM
 * @author: gaoyanfei3
 * @datetime: 2021/12/27 19:00 Monday
 */
public final class RestStatus implements Serializable {
    @Serial
    private static final long serialVersionUID = -2014592760065018707L;
    public static final RestStatus OK = new RestStatus(0, "OK");
    public static final RestStatus INTERNAL_ERROR = new RestStatus(1000, "服务器内部错误");
    public static final RestStatus PARAM_MISS = new RestStatus(1001, "缺少参数");
    public static final RestStatus PARAM_ERROR = new RestStatus(1002, "参数错误");

    private int code;
    private String message;

    public RestStatus() {
    }

    public RestStatus(int code, String msg) {
        this.code = code;
        this.message = msg;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
