package com.github.community.controller;

import com.github.community.annotation.LoginRequired;
import com.github.community.entity.Comment;
import com.github.community.entity.DiscussPost;
import com.github.community.entity.Page;
import com.github.community.entity.User;
import com.github.community.service.CommentService;
import com.github.community.service.DiscussPostService;
import com.github.community.service.UserService;
import com.github.community.util.Constant;
import com.github.community.util.HostHolder;
import com.github.community.util.MyUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
        return MyUtil.getJSONString(0, "发布成功");

    }

    @GetMapping("/detail/{discussPostId}")
    public String getDiscussPost(@PathVariable Integer discussPostId, Model model, Page page) {
        DiscussPost post = discussPostService.getDiscussPostById(discussPostId);
        model.addAttribute("post", post);
        User user = userService.getUserById(post.getUserId());
        model.addAttribute("user", user);

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
}
