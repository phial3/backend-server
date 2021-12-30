package org.example.demo.base;

import org.apache.commons.lang3.StringUtils;
import org.phial.mybatisx.common.utils.SecretKeyStore;

public class AESKey {

    private String key;

    private String iv;

    private SecretKeyStore secretKeyStore;

    public AESKey() {
    }

    public AESKey(String key, String iv) {
        this.key = key;
        this.iv = iv;
    }

    public SecretKeyStore secretKeyStore() {
        if (secretKeyStore == null) {
            this.secretKeyStore = new SecretKeyStore(key, iv);
        }
        return this.secretKeyStore;
    }

    public boolean verify() {
        return StringUtils.isNotBlank(key) && StringUtils.isNotBlank(iv) && key.length() == 32 && iv.length() == 16;
    }

    /**
     * 获取 key
     *
     * @return key
     */
    public String getKey() {
        return key;
    }

    /**
     * 设置 key
     *
     * @param key key 值
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * 获取 iv
     *
     * @return iv
     */
    public String getIv() {
        return iv;
    }

    /**
     * 设置 iv
     *
     * @param iv iv 值
     */
    public void setIv(String iv) {
        this.iv = iv;
    }
}
