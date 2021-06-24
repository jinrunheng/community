package com.github.community.controller;

import com.github.community.entity.Comment;
import com.github.community.entity.DiscussPost;
import com.github.community.entity.Event;
import com.github.community.entity.Page;
import com.github.community.kafka.EventProducer;
import com.github.community.service.CommentService;
import com.github.community.service.DiscussPostService;
import com.github.community.util.Constant;
import com.github.community.util.HostHolder;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
@RequestMapping("/comment")
public class CommentController implements Constant {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private EventProducer producer;

    @PostMapping("/add/{discussPostId}")
    public String addComment(@PathVariable int discussPostId, Comment comment) {
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        // 触发评论事件
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId", discussPostId);

        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            DiscussPost target = discussPostService.getDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        } else if (comment.getEntityType() == ENTITY_TYPE_COMMENT) {
            Comment target = commentService.getCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        producer.fireEvent(event);
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            event = new Event()
                    .setTopic(TOPIC_PUBLISH)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(ENTITY_TYPE_POST)
                    .setEntityId(discussPostId);
            producer.fireEvent(event);
        }
        return "redirect:/discuss/detail/" + discussPostId;
    }

    @GetMapping("/all/{userId}")
    public String getAllCommentsByUserId(@PathVariable Integer userId, Page page, Model model) {
        page.setLimit(10);
        int count = commentService.getCommentsCountByUserId(userId);
        model.addAttribute("count", count);
        page.setRows(count);
        page.setPath("/comment/all/" + userId);
        model.addAttribute("userId", userId);
        List<Comment> commentList = commentService.getCommentsByUserId(userId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> comments = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                Map<String, Object> map = new HashMap<>();
                map.put("content", comment.getContent());
                int entityId = comment.getEntityId();
                DiscussPost post = discussPostService.getDiscussPostById(entityId);
                map.put("title", post.getTitle());
                map.put("discussPostId", post.getId());
                map.put("createTime", comment.getCreateTime());
                comments.add(map);
            }
        }
        model.addAttribute("comments", comments);
        return "/site/my-reply";
    }


}
