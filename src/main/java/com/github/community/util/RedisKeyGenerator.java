package com.github.community.util;

public class RedisKeyGenerator {

    // 点赞功能使用 Redis 存储
    // 存储形式 redis set
    // like:entity:entityType:entityId -> set(userId)
    public static String getEntityLikeKey(int entityType, int entityId) {
        return "like:entity:" + entityType + entityId;
    }
}
