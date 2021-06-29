package com.github.community.quartz;

import com.github.community.entity.DiscussPost;
import com.github.community.service.DiscussPostService;
import com.github.community.service.ElasticsearchService;
import com.github.community.service.LikeService;
import com.github.community.util.Constant;
import com.github.community.util.RedisKeyGenerator;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostScoreRefreshJob implements Job, Constant {

    private static Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    // 项目纪元
    private static final Date EPOCH;

    static {
        try {
            EPOCH = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-08-01 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("init EPOCH fail..." + e);
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String redisKey = RedisKeyGenerator.getPostScoreKey();
        BoundSetOperations boundSetOperations = redisTemplate.boundSetOps(redisKey);
        if (boundSetOperations.size() == 0) {
            logger.info("没有需要刷新的帖子");
            return;
        }
        logger.info("正在刷新帖子分数" + boundSetOperations.size());
        while (boundSetOperations.size() > 0) {
            this.refresh((Integer) boundSetOperations.pop());
        }
        logger.info("帖子分数刷新完毕");
    }

    private void refresh(int postId) {

        DiscussPost post = discussPostService.getDiscussPostById(postId);
        if (post == null) {
            logger.error("该帖子不存在 id = " + postId);
            return;
        }

        // 是否加精
        boolean isWonderful = post.getStatus() == 1;
        // 取出评论数量
        int commentCount = post.getCommentCount();
        // 点赞数量
        int likeCount = (int) likeService.findEntityLikeCount(Constant.ENTITY_TYPE_POST, postId);

        // 计算权重
        double weight = (isWonderful ? 75 : 0) + commentCount * 10 + likeCount * 2;
        // 分数
        double score = Math.log10(Math.max(weight, 1)) +
                (post.getCreateTime().getTime() - EPOCH.getTime()) / (1000 * 3600 * 24);

        // 更新帖子分数
        discussPostService.updateScore(postId, score);

        // 同步搜索的数据
        post.setScore(score);
        elasticsearchService.saveDiscussPost(post);
    }
}
