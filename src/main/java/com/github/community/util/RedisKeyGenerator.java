package com.github.community.util;

public class RedisKeyGenerator {

    // 某个实体收到的赞
    // 点赞功能使用 Redis 存储
    // 存储形式 redis set
    // like:entity:entityType:entityId -> set(userId)
    public static String getEntityLikeKey(int entityType, int entityId) {
        return "like:entity:" + entityType + entityId;
    }

    // 某个用户收到的赞
    // like:user:userId -> int
    public static String getUserLikeKey(int userId) {
        return "like:user:" + userId;
    }

}
