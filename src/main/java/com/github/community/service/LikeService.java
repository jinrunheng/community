package com.github.community.service;

import com.github.community.util.Constant;
import com.github.community.util.MyUtil;
import com.github.community.util.RedisKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService implements Constant {

    @Autowired
    private RedisTemplate redisTemplate;

    // 点赞功能
    public void like(int userId, int entityType, int entityId, int entityUserId) {
//        String entityLikeKey = RedisKeyGenerator.getEntityLikeKey(entityType, entityId);
//
//        boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
//
//        if (isMember) {
//            redisTemplate.opsForSet().remove(entityLikeKey, userId);
//        } else {
//            redisTemplate.opsForSet().add(entityLikeKey, userId);
//        }

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String entityLikeKey = RedisKeyGenerator.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyGenerator.getUserLikeKey(entityUserId);
                boolean isMember = redisOperations.opsForSet().isMember(entityLikeKey, userId);
                redisOperations.multi();
                // 第一次点赞为点赞,实体拥有的用户收到的赞 + 1
                // 第二次点赞为取消,实体拥有的用户收到的赞 -1
                if (isMember) {
                    redisOperations.opsForSet().remove(entityLikeKey, userId);
                    redisOperations.opsForValue().decrement(userLikeKey);
                } else {
                    redisOperations.opsForSet().add(entityLikeKey, userId);
                    redisOperations.opsForValue().increment(userLikeKey);
                }
                return redisOperations.exec();
            }
        });


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

    // 查询某个用户获得赞的总数
    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyGenerator.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        if (count == null) {
            return 0;
        }
        return count;
    }
}
