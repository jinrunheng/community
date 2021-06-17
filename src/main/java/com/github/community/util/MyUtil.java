package com.github.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
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

    public static String getJSONString(int code, String msg, Map<String, Object> map) {
        JSONObject jsonObject = new JSONObject();

        if (!Objects.isNull(map)) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                jsonObject.put(entry.getKey(), entry.getValue());
            }
        }
        jsonObject.put("code", code);
        if (!Objects.isNull(msg)) {
            jsonObject.put("msg", msg);
        }
        return jsonObject.toJSONString();
    }

    public static String getJSONString(int code, String msg) {
        return getJSONString(code, msg, null);
    }

    public static String getJSONString(int code) {
        return getJSONString(code, null);
    }

    public static String getFileSuffix(String fileName) {
        int i = fileName.lastIndexOf(".");
        if (i == -1) {
            return null;
        }
        return fileName.substring(i + 1);
    }

    public static boolean isImg(String suffix) {
        if (suffix.equals("png")
                || suffix.equals("jpg")
                || suffix.equals("jpeg")
        ) {
            return true;
        }
        return false;
    }

}
