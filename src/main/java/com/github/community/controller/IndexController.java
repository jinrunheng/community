package com.github.community.controller;

import com.github.community.entity.DiscussPost;
import com.github.community.entity.Page;
import com.github.community.entity.User;
import com.github.community.service.DiscussPostService;
import com.github.community.service.LikeService;
import com.github.community.service.UserService;
import com.github.community.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class IndexController implements Constant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @GetMapping("/index")
    public String getIndex(Model model, Page page) {
        page.setRows(discussPostService.getDiscussPostCount(null));
        page.setPath("/index");

        // 前十条数据显示到首页
        List<DiscussPost> discussPosts = discussPostService.getDiscussPosts(null, page.getOffset(), page.getLimit());
        List<Map<String, Object>> list = new ArrayList<>();
        if (discussPosts != null) {
            for (DiscussPost discussPost : discussPosts) {
                User user = userService.getUserById(discussPost.getUserId());
                Map<String, Object> map = new HashMap<>();
                map.put("discussPost", discussPost);
                map.put("user", user);
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPost.getId());
                map.put("likeCount", likeCount);
                list.add(map);
            }
        }
        model.addAttribute("list", list);
        model.addAttribute("page", page);
        return "/index";
    }

    @GetMapping("/error")
    public String getErrorPage() {
        return "/error/500";
    }

    @GetMapping("/denied")
    public String getDeniedPage() {
        return "/error/404";
    }
}
