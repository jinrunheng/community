package com.github.community.controller;

import com.github.community.entity.Comment;
import com.github.community.entity.DiscussPost;
import com.github.community.entity.Page;
import com.github.community.service.CommentService;
import com.github.community.service.DiscussPostService;
import com.github.community.util.Constant;
import com.github.community.util.HostHolder;
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

    @PostMapping("/add/{discussPostId}")
    public String addComment(@PathVariable int discussPostId, Comment comment) {
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);
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
