package com.github.community.controller;

import com.github.community.annotation.LoginRequired;
import com.github.community.entity.*;
import com.github.community.kafka.EventProducer;
import com.github.community.service.CommentService;
import com.github.community.service.DiscussPostService;
import com.github.community.service.LikeService;
import com.github.community.service.UserService;
import com.github.community.util.Constant;
import com.github.community.util.HostHolder;
import com.github.community.util.MyUtil;
import com.github.community.util.RedisKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements Constant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private EventProducer producer;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (Objects.isNull(user)) {
            return MyUtil.getJSONString(403, "未登陆！");
        }
        DiscussPost post = DiscussPost.builder()
                .userId(user.getId())
                .title(title)
                .content(content)
                .type(0)
                .status(0)
                .createTime(new Date())
                .commentCount(0)
                .score(0.0)
                .build();

        discussPostService.addDiscussPost(post);

        // 触发发帖事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(post.getId());
        producer.fireEvent(event);
        // 热帖排行处理评分
        String redisKey = RedisKeyGenerator.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey, post.getId());


        return MyUtil.getJSONString(0, "发布成功");

    }

    @GetMapping("/all/{userId}")
    public String getAllDiscussPostsByUserId(@PathVariable("userId") Integer userId, Page page, Model model) {
        User user = userService.getUserById(userId);
        if (Objects.isNull(user)) {
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("userId", userId);
        int discussPostCount = discussPostService.getDiscussPostCount(userId);
        model.addAttribute("count", discussPostCount);

        page.setLimit(5);
        page.setPath("/discuss/all/" + userId);
        page.setRows(discussPostCount);

        List<DiscussPost> discussPosts = discussPostService.getDiscussPosts(userId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> posts = new ArrayList<>();
        if (!Objects.isNull(discussPosts)) {
            for (DiscussPost discussPost : discussPosts) {
                Map<String, Object> map = new HashMap<>();
                map.put("discussPostId", discussPost.getId());
                map.put("title", discussPost.getTitle());
                map.put("content", discussPost.getContent());
                map.put("createTime", discussPost.getCreateTime());
                long like = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPost.getId());
                map.put("like", like);
                posts.add(map);
            }
        }
        model.addAttribute("posts", posts);
        return "/site/my-post";
    }

    @GetMapping("/detail/{discussPostId}")
    public String getDiscussPost(@PathVariable Integer discussPostId, Model model, Page page) {
        DiscussPost post = discussPostService.getDiscussPostById(discussPostId);
        model.addAttribute("post", post);
        User user = userService.getUserById(post.getUserId());
        model.addAttribute("user", user);
        // 获取点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeCount", likeCount);
        // 获取点赞状态
        int likeStatus = 0;
        if (hostHolder.getUser() != null) {
            likeStatus = likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_POST, discussPostId);
        }
        model.addAttribute("likeStatus", likeStatus);

        // 帖子评论的分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());
        List<Comment> comments = commentService.getCommentsByEntityTypeAndId(ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (!Objects.isNull(comments)) {
            for (Comment comment : comments) {
                Map<String, Object> commentVo = new HashMap<>();
                // 评论
                commentVo.put("comment", comment);
                // 评论的作者
                commentVo.put("commentUser", userService.getUserById(comment.getUserId()));
                // 获取点赞数量
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeCount", likeCount);
                // 获取点赞状态
                likeStatus = 0;
                if (hostHolder.getUser() != null) {
                    likeStatus = likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, comment.getId());
                }
                commentVo.put("likeStatus", likeStatus);
                // 回复列表
                List<Comment> replies = commentService.getCommentsByEntityTypeAndId(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                // 回复的 VO 列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (!Objects.isNull(replies)) {
                    for (Comment reply : replies) {
                        Map<String, Object> replyVo = new HashMap<>();
                        // 回复
                        replyVo.put("reply", reply);
                        // 回复的作者
                        replyVo.put("replyUser", userService.getUserById(reply.getUserId()));
                        // 回复的目标
                        User target = reply.getTargetId() == 0 ? null : userService.getUserById(reply.getTargetId());
                        // 获取点赞数量
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeCount", likeCount);
                        // 获取点赞状态
                        likeStatus = 0;
                        if (hostHolder.getUser() != null) {
                            likeStatus = likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, reply.getId());
                        }
                        replyVo.put("likeStatus", likeStatus);
                        replyVo.put("target", target);
                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replyVoList", replyVoList);
                // 回复的数量
                int replyCount = commentService.getCommentsCountByEntityTypeAndId(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);
                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("commentVoList", commentVoList);


        return "/site/discuss-detail";
    }

    // 置顶
    // 异步请求
    // type : 0 为普通帖子，1 为置顶帖子
    @PostMapping("/top")
    @ResponseBody
    public String setTopType(int id) {
        discussPostService.updateDiscussPostType(id, DISCUSS_TYPE_TOP);
        // 触发更新帖子事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        producer.fireEvent(event);
        return MyUtil.getJSONString(0);
    }

    // 加精
    // 异步请求
    // status : 0 为普通，1 为加精，2 为拉黑（删除）
    @PostMapping("/wonderful")
    @ResponseBody
    public String setWonderfulStatus(int id) {
        discussPostService.updateDiscussPostStatus(id, DISCUSS_STATUS_WONDERFUL);
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        producer.fireEvent(event);
        // 热帖排行处理加精
        String redisKey = RedisKeyGenerator.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey, id);
        return MyUtil.getJSONString(0);
    }

    // 删除
    // 异步请求
    @PostMapping("/delete")
    @ResponseBody
    public String setDeleteStatus(int id) {
        discussPostService.updateDiscussPostStatus(id, DISCUSS_STATUS_DELETE);
        // 触发删帖事件
        Event event = new Event()
                .setTopic(TOPIC_DELETE)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        producer.fireEvent(event);
        return MyUtil.getJSONString(0);
    }
}
