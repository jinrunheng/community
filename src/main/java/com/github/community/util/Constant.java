package com.github.community.util;

public interface Constant {

    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 0;

    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT = 1;

    /**
     * 激活失败
     */
    int ACTIVATION_FAILURE = 2;

    /**
     * 默认状态登陆凭证超时时间:1天
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /**
     * 勾选 rememberMe 之后登陆凭证超时时间:100天
     */
    int REMEMBER_EXPIRED_SECONDS = 3600 * 12 * 100;
}
