package com.github.community.controller;

import com.github.community.entity.Event;
import com.github.community.entity.User;
import com.github.community.kafka.EventProducer;
import com.github.community.service.LikeService;
import com.github.community.util.Constant;
import com.github.community.util.HostHolder;
import com.github.community.util.MyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController implements Constant {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer producer;

    @PostMapping("/like")
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId,int postId) {
        User user = hostHolder.getUser();
        //  点赞
        likeService.like(user.getId(), entityType, entityId, entityUserId);
        // 获取点赞的数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        // 获取点赞的状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);

        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        // 触发点赞事件
        if(likeStatus == LIKE_STATUS_YES){
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(user.getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId",postId);

            producer.fireEvent(event);
        }

        return MyUtil.getJSONString(0, null, map);
    }
}
