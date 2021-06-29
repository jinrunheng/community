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

    // 获取某个用户关注的实体，关注的对象可能是用户，帖子等等
    // followee:userId:entityType -> zset(entityId,now)
    public static String getFolloweeKey(int userId, int entityType) {
        return "followee:" + userId + ":" + entityType;
    }

    // 获取某个实体拥有的关注者
    // follower:entityType:entityId -> zset(userId,now)
    public static String getFollowerKey(int entityType, int entityId) {
        return "follower:" + entityType + ":" + entityId;
    }

    // 将登陆验证码缓存到 redis 中
    // 获取登陆验证码
    // owner 是随机生成的字符串，缓存到cookie中
    public static String getKaptchaKey(String owner) {
        return "kaptcha:" + owner;
    }

    // 获取登陆凭证
    public static String getTicketKey(String ticket) {
        return "ticket:" + ticket;
    }

    // 获取用户
    public static String getUserKey(int userId) {
        return "user:" + userId;
    }

    // 获取 单日 UV （UV：独立访客）
    public static String getUVKey(String date) {
        return "uv:" + date;
    }

    // 获取 startDate ~ endDate 区间的 UV
    public static String getUVKey(String startDate, String endDate) {
        return "uv:" + startDate + ":" + endDate;
    }

    // 获取单日活跃用户
    public static String getDAUKey(String date) {
        return "dau:" + date;
    }

    // 获取 startDate ~ endDate 区间的 活跃用户
    public static String getDAUKey(String startDate, String endDate) {
        return "dau:" + startDate + ":" + endDate;
    }

    // 统计帖子分数
    public static String getPostScoreKey() {
        return "post:score";
    }
}
