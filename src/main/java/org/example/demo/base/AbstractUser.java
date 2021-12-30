package org.example.demo.base;

import org.phial.mybatisx.api.annotation.Column;
import org.phial.mybatisx.api.entity.LongEditableEntity;
import org.phial.mybatisx.api.enums.DataType;

/**
 * 用户抽象
 * @since 2021/4/8
 * @author phial
 */
public abstract class AbstractUser extends LongEditableEntity {

    public AbstractUser() {
    }

    public AbstractUser(Long id) {
        super(id);
    }

    public AbstractUser(String username) {
        this.username = username;
        this.loginTime = System.currentTimeMillis();
    }

    public AbstractUser(Long id, String username) {
        super(id);
        this.loginTime = System.currentTimeMillis();
    }

    public AbstractUser(String username, long loginTime) {
        this.username = username;
        this.loginTime = loginTime;
    }

    public AbstractUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Column(comment = "用户名", type = DataType.VARCHAR, length = "32")
    private String username;

    private long loginTime;

    @Column(comment = "密码", type = DataType.VARCHAR, length = "255")
    private String password;

    @Column(comment = "备注", type = DataType.VARCHAR, length = "500")
    private String description;

    /**
     * 是否启用
     */
    @Column(comment = "是否启用", type = DataType.BIT, length = "1")
    private Boolean enabled;

    /**
     * 登录成功后颁发的令牌
     */
    private String token;


    @Column(comment = "接口调用安全码", type = DataType.VARCHAR, length = "64")
    private String secretKey;

    /**
     * 获取 username
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置 username
     *
     * @param username username 值
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取 loginTime
     *
     * @return loginTime
     */
    public long getLoginTime() {
        return loginTime;
    }

    /**
     * 设置 loginTime
     *
     * @param loginTime loginTime 值
     */
    public void setLoginTime(long loginTime) {
        this.loginTime = loginTime;
    }

    /**
     * 获取 password
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置 password
     *
     * @param password password 值
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取 description
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置 description
     *
     * @param description description 值
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取 enabled
     *
     * @return enabled
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * 设置 enabled
     *
     * @param enabled enabled 值
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 获取 token
     *
     * @return token
     */
    public String getToken() {
        return token;
    }

    /**
     * 设置 token
     *
     * @param token token 值
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * 获取 secretKey
     *
     * @return secretKey
     */
    public String getSecretKey() {
        return secretKey;
    }

    /**
     * 设置 secretKey
     *
     * @param secretKey secretKey 值
     */
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
