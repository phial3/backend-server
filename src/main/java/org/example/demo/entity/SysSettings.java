package org.example.demo.entity;

import org.example.demo.base.AttributeItem;

/**
 * 系统设置
 * @since 2021/4/8
 * @author mayanjun
 */
public class SysSettings {

    /**
     * 是都打印详细日志
     */
    private Boolean verboseLogEnabled = false;

    /**
     * 默认菜单是否展开
     */
    private Boolean defaultExpand = true;

    /**
     * 默认菜单展开是与其他展开的菜单是否互斥
     */
    private Boolean defaultExpandExclusively = true;

    private String notifierMailAddress = "ihomeops@jd.com";

    private String notifierMailPassword = "";

    @AttributeItem
    public Boolean getVerboseLogEnabled() {
        return verboseLogEnabled;
    }

    public void setVerboseLogEnabled(Boolean verboseLogEnabled) {
        this.verboseLogEnabled = verboseLogEnabled;
    }

    @AttributeItem(user = "*")
    public Boolean getDefaultExpand() {
        return defaultExpand;
    }

    public void setDefaultExpand(Boolean defaultExpand) {
        this.defaultExpand = defaultExpand;
    }

    /**
     * 获取 defaultExpandExclusively
     *
     * @return defaultExpandExclusively
     */
    @AttributeItem(user = "*")
    public Boolean getDefaultExpandExclusively() {
        return defaultExpandExclusively;
    }

    /**
     * 设置 defaultExpandExclusively
     *
     * @param defaultExpandExclusively defaultExpandExclusively 值
     */
    public void setDefaultExpandExclusively(Boolean defaultExpandExclusively) {
        this.defaultExpandExclusively = defaultExpandExclusively;
    }

    /**
     * 获取 notifierMailAddress
     *
     * @return notifierMailAddress
     */
    @AttributeItem
    public String getNotifierMailAddress() {
        return notifierMailAddress;
    }

    /**
     * 设置 notifierMailAddress
     *
     * @param notifierMailAddress notifierMailAddress 值
     */
    public void setNotifierMailAddress(String notifierMailAddress) {
        this.notifierMailAddress = notifierMailAddress;
    }

    /**
     * 获取 notifierMailPassword
     *
     * @return notifierMailPassword
     */
    @AttributeItem
    public String getNotifierMailPassword() {
        return notifierMailPassword;
    }

    /**
     * 设置 notifierMailPassword
     *
     * @param notifierMailPassword notifierMailPassword 值
     */
    public void setNotifierMailPassword(String notifierMailPassword) {
        this.notifierMailPassword = notifierMailPassword;
    }
}