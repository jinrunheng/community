package com.github.community.kafka;

import com.alibaba.fastjson.JSONObject;
import com.github.community.entity.DiscussPost;
import com.github.community.entity.Event;
import com.github.community.entity.Message;
import com.github.community.service.DiscussPostService;
import com.github.community.service.ElasticsearchService;
import com.github.community.service.MessageService;
import com.github.community.util.Constant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements Constant {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private MessageService messageService;

    // 发帖事件
    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublishMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息内容为空");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误");
            return;
        }
        DiscussPost post = discussPostService.getDiscussPostById(event.getEntityId());
        elasticsearchService.saveDiscussPost(post);
    }

    // 删帖事件
    @KafkaListener(topics = {TOPIC_DELETE})
    public void handleDeleteMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息内容为空");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误");
            return;
        }
        elasticsearchService.deleteDiscussPost(event.getEntityId());
    }

    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW})
    public void handleMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息内容为空");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误");
            return;
        }

        // 发送通知 为系统发送
        // 系统通知的格式：例如--->用户 *nowcoder* 评论的你的帖子,*点击查看（链接到帖子，需要帖子的 id）*
        // 所以，from_id 为 1
        // conversation_id 就没有必要拼成 from_id_to_id 的格式了
        // conversation_id 可以直接存Topic
        // content 直接存 event 的 jsonString 即可
        Message message = Message.builder()
                .fromId(SYSTEM_USER_ID)
                .toId(event.getEntityUserId())
                .conversationId(event.getTopic())
                .createTime(new Date())
                .build();

        Map<String, Object> content = new HashMap<>();

        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());

        if (!event.getData().isEmpty()) {
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }


}
