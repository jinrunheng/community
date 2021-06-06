package com.github.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class MyUtil {

    // 生成随机字符串
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    // MD5 加密
    public static String md5(String rawPassword) {
        if (StringUtils.isBlank(rawPassword)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(rawPassword.getBytes());
    }

}
