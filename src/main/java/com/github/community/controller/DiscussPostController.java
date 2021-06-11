package com.github.community.controller;

import com.github.community.annotation.LoginRequired;
import com.github.community.entity.DiscussPost;
import com.github.community.entity.User;
import com.github.community.service.DiscussPostService;
import com.github.community.service.UserService;
import com.github.community.util.HostHolder;
import com.github.community.util.MyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Objects;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

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
    public String getDiscussPost(@PathVariable Integer discussPostId, Model model) {
        DiscussPost post = discussPostService.getDiscussPostById(discussPostId);
        model.addAttribute("post", post);
        User user = userService.getUserById(post.getUserId());
        model.addAttribute("user", user);
        return "/site/discuss-detail";
    }
}
