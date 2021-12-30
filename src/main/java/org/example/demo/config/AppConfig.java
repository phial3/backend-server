package org.example.demo.config;

import org.example.demo.base.AESKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @since 2019-10-10
 */
@Component
@ConfigurationProperties(prefix = "app-config")
public class AppConfig implements InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(AppConfig.class);

    /**
     * 默认的API时间戳校验容忍度
     */
    private static final int DEFAULT_API_TIMESTAMP_TOLERANCE = 30;

    private String buildVersion;
    private String profile;

    /**
     * 当前系统使用的域名
     */
    private String domain;

    /**
     * 后台登录的TOKEN名称
     */
    private String tokenCookieName = "token";

    private AESKey consoleAesKey;


    private String systemName = "后台管理系统";

    private String redisHost = "127.0.0.1";
    private int redisPort = 6379;
    private boolean redisEnabled = false;

    private int serverPort;

    /**
     * 是否验证API时间戳的时效性，如果开启则调用接口时传入的时间戳参数与服务器时间差不能大于 apiTimestampTolerance 秒
     */
    private boolean verifyApiTimestamp = true;

    private int apiTimestampTolerance = DEFAULT_API_TIMESTAMP_TOLERANCE;

    public String getBuildVersion() {
        return buildVersion;
    }

    public void setBuildVersion(String buildVersion) {
        this.buildVersion = buildVersion;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        // 修正参数
        if (this.verifyApiTimestamp) {
            if (apiTimestampTolerance <= 0) {
                apiTimestampTolerance = DEFAULT_API_TIMESTAMP_TOLERANCE; // 强制修正为5秒
            }
        }
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getTokenCookieName() {
        return tokenCookieName;
    }

    public void setTokenCookieName(String tokenCookieName) {
        this.tokenCookieName = tokenCookieName;
    }

    ///////////////////////////////////////////////////////////////////////////////////////

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    /**
     * 获取 redisHost
     *
     * @return redisHost
     */
    public String getRedisHost() {
        return redisHost;
    }

    /**
     * 设置 redisHost
     *
     * @param redisHost redisHost 值
     */
    public void setRedisHost(String redisHost) {
        this.redisHost = redisHost;
    }

    /**
     * 获取 redisPort
     *
     * @return redisPort
     */
    public int getRedisPort() {
        return redisPort;
    }

    /**
     * 设置 redisPort
     *
     * @param redisPort redisPort 值
     */
    public void setRedisPort(int redisPort) {
        this.redisPort = redisPort;
    }

    /**
     * 获取 redisEnabled
     *
     * @return redisEnabled
     */
    public boolean isRedisEnabled() {
        return redisEnabled;
    }

    /**
     * 设置 redisEnabled
     *
     * @param redisEnabled redisEnabled 值
     */
    public void setRedisEnabled(boolean redisEnabled) {
        this.redisEnabled = redisEnabled;
    }

    /**
     * 获取 consoleAesKey
     *
     * @return consoleAesKey
     */
    public AESKey getConsoleAesKey() {
        return consoleAesKey;
    }

    /**
     * 设置 consoleAesKey
     *
     * @param consoleAesKey consoleAesKey 值
     */
    public void setConsoleAesKey(AESKey consoleAesKey) {
        this.consoleAesKey = consoleAesKey;
    }

    /**
     * 获取 verifyApiTimestamp
     *
     * @return verifyApiTimestamp
     */
    public boolean isVerifyApiTimestamp() {
        return verifyApiTimestamp;
    }

    /**
     * 设置 verifyApiTimestamp
     *
     * @param verifyApiTimestamp verifyApiTimestamp 值
     */
    public void setVerifyApiTimestamp(boolean verifyApiTimestamp) {
        this.verifyApiTimestamp = verifyApiTimestamp;
    }

    /**
     * 获取 apiTimestampTolerance
     *
     * @return apiTimestampTolerance
     */
    public int getApiTimestampTolerance() {
        return apiTimestampTolerance;
    }

    /**
     * 设置 apiTimestampTolerance
     *
     * @param apiTimestampTolerance apiTimestampTolerance 值
     */
    public void setApiTimestampTolerance(int apiTimestampTolerance) {
        this.apiTimestampTolerance = apiTimestampTolerance;
    }

    /**
     * 获取 profile
     *
     * @return profile
     */
    public String getProfile() {
        return profile;
    }

    /**
     * 设置 profile
     *
     * @param profile profile 值
     */
    public void setProfile(String profile) {
        this.profile = profile;
    }

    /**
     * 获取 serverPort
     *
     * @return serverPort
     */
    public int getServerPort() {
        return serverPort;
    }

    /**
     * 设置 serverPort
     *
     * @param serverPort serverPort 值
     */
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
}
