package org.example.demo.base;


import org.phial.mybatisx.common.Status;

/**
 * @author phial
 * @vendor phial.org
 * @generator consolegen 1.0
 * @manufacturer https://phial.org
 * @since 2019-10-10
 */
public interface StatusCode {

    Status USER_DISABLE = new Status(2001, "该账号被禁用，请联系管理员！");
    Status DAO_SAVE_FAIL = new Status(3001, "保存失败");
    Status DAO_UPDATE_FAIL = new Status(3002, "更新失败");
    Status API_NOT_SUPPORTED = new Status(3003, "不支持的API");
    Status PERMISSION_DENIED = new Status(3004, "无权限");
    Status OPERATION_NOT_SUPPORTED = new Status(3005, "不支持的操作");
    Status ILLEGAL_ACCESS = new Status(3006, "非法访问");
    Status OPEN_API_PERMISSION_DENIED = new Status(3500, "非法访问");
    Status NOT_LOGIN = new Status(3600, "非法登录");

}
