package com.github.community.service;

import com.github.community.entity.User;
import com.github.community.util.Constant;
import com.github.community.util.RedisKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FollowService implements Constant {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    // 关注
    // 用户关注了某个实体
    // followee:userId:entityType -> zset(entityId,now)
    // follower:entityType:entityId -> zset(userId,now)
    public void follow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String followeeKey = RedisKeyGenerator.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyGenerator.getFollowerKey(entityType, entityId);

                redisOperations.multi();
                redisOperations.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());
                redisOperations.opsForZSet().add(followerKey, userId, System.currentTimeMillis());

                return redisOperations.exec();
            }
        });
    }

    // 取消关注
    public void unFollow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String followeeKey = RedisKeyGenerator.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyGenerator.getFollowerKey(entityType, entityId);
                redisOperations.multi();
                redisOperations.opsForZSet().remove(followeeKey, entityId);
                redisOperations.opsForZSet().remove(followerKey, userId);

                return redisOperations.exec();
            }
        });
    }

    // 查询某个用户关注的实体的数量
    public long findFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyGenerator.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    // 查询某个实体的关注者数量
    public long findFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyGenerator.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    // 查询当前用户是否已经关注该实体
    public boolean hasFollowed(int userId, int entityType, int entityId) {
        String followeeKey = RedisKeyGenerator.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;
    }

    private List<Map<String, Object>> getTargetUsers(int offset, int limit, String key) {
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(key, offset, offset + limit - 1);
        if (targetIds == null) {
            return null;
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (Integer targetId : targetIds) {
            Map<String, Object> map = new HashMap<>();
            User user = userService.getUserById(targetId);
            map.put("user", user);
            Double score = redisTemplate.opsForZSet().score(key, targetId);
            map.put("followTime", new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }

    // 查询某用户关注的人
    // 支持分页操作
    public List<Map<String, Object>> findFollowees(int userId, int offset, int limit) {
        String followeeKey = RedisKeyGenerator.getFolloweeKey(userId, ENTITY_TYPE_USER);
        return getTargetUsers(offset, limit, followeeKey);
    }



    // 查询某个用户的关注者
    // 支持分页操作
    public List<Map<String, Object>> findFollowers(int userId, int offset, int limit) {
        String followerKey = RedisKeyGenerator.getFollowerKey(ENTITY_TYPE_USER, userId);
        return getTargetUsers(offset, limit, followerKey);
    }
}
