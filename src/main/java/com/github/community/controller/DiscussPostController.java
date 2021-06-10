package com.github.community.controller;

import com.github.community.annotation.LoginRequired;
import com.github.community.entity.DiscussPost;
import com.github.community.entity.User;
import com.github.community.service.DiscussPostService;
import com.github.community.util.HostHolder;
import com.github.community.util.MyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.Objects;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

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

}
