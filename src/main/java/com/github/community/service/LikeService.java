package com.github.community.service;

import com.github.community.util.Constant;
import com.github.community.util.RedisKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LikeService implements Constant {

    @Autowired
    private RedisTemplate redisTemplate;

    // 点赞功能
    public void like(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyGenerator.getEntityLikeKey(entityType, entityId);
        // 第一次点赞为点赞
        // 第二次点赞为取消
        boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);

        if (isMember) {
            redisTemplate.opsForSet().remove(entityLikeKey, userId);
        } else {
            redisTemplate.opsForSet().add(entityLikeKey, userId);
        }


    }

    // 查询某个实体被点赞的数量
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyGenerator.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    // 查询某人对某实体的点赞状态，有没有点过赞
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyGenerator.getEntityLikeKey(entityType, entityId);

        if (redisTemplate.opsForSet().isMember(entityLikeKey, userId)) {
            return LIKE_STATUS_YES;
        } else {
            return LIKE_STATUS_NO;
        }
    }
}
