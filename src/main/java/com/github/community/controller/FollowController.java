package com.github.community.controller;

import com.github.community.annotation.LoginRequired;
import com.github.community.entity.Page;
import com.github.community.entity.User;
import com.github.community.service.FollowService;
import com.github.community.service.UserService;
import com.github.community.util.Constant;
import com.github.community.util.HostHolder;
import com.github.community.util.MyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
public class FollowController implements Constant {

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    // 关注
    // ajax
    @PostMapping("/follow")
    @ResponseBody
    @LoginRequired
    public String follow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        followService.follow(user.getId(), entityType, entityId);
        return MyUtil.getJSONString(0, "已关注");
    }

    // 取消
    @PostMapping("/unfollow")
    @ResponseBody
    public String unFollow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        followService.unFollow(user.getId(), entityType, entityId);
        return MyUtil.getJSONString(0, "已取消关注");
    }

    @GetMapping("/followees/{userId}")
    public String getFollowees(@PathVariable("userId") int userId, Model model, Page page) {
        User user = userService.getUserById(userId);
        if (Objects.isNull(user)) {
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user", user);
        // 每一页显示5条数据
        page.setLimit(5);
        page.setRows((int) followService.findFolloweeCount(userId, ENTITY_TYPE_USER));
        page.setPath("/followees/" + userId);

        List<Map<String, Object>> followees = followService.findFollowees(userId, page.getOffset(), page.getLimit());
        addIsMutualInformation(followees);
        model.addAttribute("followees", followees);
        return "/site/followee";
    }

    @GetMapping("/followers/{userId}")
    public String getFollowers(@PathVariable("userId") int userId, Model model, Page page) {
        User user = userService.getUserById(userId);
        if (Objects.isNull(user)) {
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user", user);
        // 每一页显示5条数据
        page.setLimit(5);
        page.setPath("/followers/" + userId);
        page.setRows((int) followService.findFollowerCount(ENTITY_TYPE_USER, userId));

        List<Map<String, Object>> followers = followService.findFollowers(userId, page.getOffset(), page.getLimit());
        addIsMutualInformation(followers);
        model.addAttribute("followers", followers);
        return "/site/follower";
    }

    private void addIsMutualInformation(List<Map<String, Object>> list) {
        if (list != null) {
            for (Map<String, Object> map : list) {
                User u = (User) map.get("user");
                boolean isMutual = false;
                map.put("loginUser", null);
                // 是否互关
                if (hostHolder.getUser() != null) {
                    map.put("loginUser", hostHolder.getUser());
                    isMutual = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, u.getId());
                }
                map.put("isMutual", isMutual);
            }
        }
    }
}
