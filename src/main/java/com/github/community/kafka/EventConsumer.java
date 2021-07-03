package com.github.community.kafka;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.community.entity.DiscussPost;
import com.github.community.entity.Event;
import com.github.community.entity.Message;
import com.github.community.service.DiscussPostService;
import com.github.community.service.ElasticsearchService;
import com.github.community.service.MessageService;
import com.github.community.util.Constant;
import com.github.community.util.MyUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import javax.management.relation.RoleUnresolved;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

@Component
public class EventConsumer implements Constant {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private MessageService messageService;

    @Value("${wk.image.storage}")
    private String wkImageStorage;

    @Value("${wk.image.command}")
    private String wkImageCommand;

    @Value("${qiniu.bucket.share.name}")
    private String shareBucketName;

    @Value("${qiniu.key.access}")
    private String accessKey;

    @Value("${qiniu.key.secret}")
    private String secretKey;

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    // 发帖,更新帖子事件
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

    // 消费分享事件
    @KafkaListener(topics = TOPIC_SHARE)
    public void handleShareMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息内容为空");
            // return false;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误");
            // return false;
        }

        String htmlUrl = (String) event.getData().get("htmlUrl");
        String fileName = (String) event.getData().get("fileName");
        String suffix = (String) event.getData().get("suffix");

        String command = wkImageCommand + " --quality 75 " + htmlUrl + " " + wkImageStorage + fileName + suffix;
        try {
            Process process = Runtime.getRuntime().exec(command);
            logger.info("生成长图成功：" + command);
//            if (process.waitFor() == 0) {
//                logger.info("生成长图成功：" + command);
//                return true;
//            }
            // return false;
        } catch (IOException e) {
            logger.error("生成长图失败：" + e.getStackTrace());
        }

        // 启用定时器，监控图片，一旦生成，则上传到七牛云服务器
        UploadTask task = new UploadTask(fileName, suffix);
        Future future = taskScheduler.scheduleAtFixedRate(task, 500);
        task.setFuture(future);
        // return false;
    }


    class UploadTask implements Runnable {

        // 文件名称
        private String fileName;
        // 文件后缀
        private String suffix;

        // 启动任务的返回值
        private Future future;

        // 开始时间
        private long startTime;

        // 上传次数
        private int uploadTimes;

        public UploadTask(String fileName, String suffix) {
            this.fileName = fileName;
            this.suffix = suffix;
            this.startTime = System.currentTimeMillis();
        }

        @Override
        public void run() {
            // 生成图片失败
            if (System.currentTimeMillis() - startTime > 300000) {
                logger.error("执行时间过长，终止任务:" + fileName);
                future.cancel(true);
                return;
            }
            // 上传失败
            if (uploadTimes >= 3) {
                logger.error("上传次数过多，终止任务：" + fileName);
                future.cancel(true);
                return;
            }

            String path = wkImageStorage + fileName + suffix;
            File file = new File(path);
            if (file.exists()) {
                logger.info(String.format("开始第%d次上传[%s].", ++uploadTimes, fileName));

                // 设置响应信息
                StringMap policy = new StringMap();
                policy.put("returnBody", MyUtil.getJSONString(0));
                // 生成上传凭证
                Auth auth = Auth.create(accessKey, secretKey);
                String uploadToken = auth.uploadToken(shareBucketName, fileName, 3600, policy);
                // 指定上传的机房
                UploadManager manager = new UploadManager(new Configuration(Zone.zone2()));
                try {
                    // 开始上传图片
                    Response response = manager.put(
                            path, fileName, uploadToken, null, "image/" + suffix.substring(suffix.lastIndexOf(".") + 1), false
                    );
                    // 处理响应结果
                    JSONObject json = JSONObject.parseObject(response.bodyString());
                    if (json == null || json.get("code") == null || !json.get("code").toString().equals("0")) {
                        logger.info(String.format("第%d次上传失败[%d]", uploadTimes, file));
                    } else {
                        logger.info(String.format("第%d上传成功[%s]", uploadTimes, fileName));
                        future.cancel(true);
                    }
                } catch (QiniuException e) {
                    logger.info(String.format("第%d次上传失败[%s].", uploadTimes, fileName));
                }
            } else {
                logger.info("等待图片生成[" + fileName + "].");
            }
        }

        public void setFuture(Future future) {
            this.future = future;
        }
    }
}
