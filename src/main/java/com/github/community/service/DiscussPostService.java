package com.github.community.service;

import com.github.community.dao.DiscussPostDao;
import com.github.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostDao discussPostDao;

    public List<DiscussPost> getDiscussPosts(Integer userId, Integer offset, Integer limit) {
        return discussPostDao.findDiscussPosts(userId, offset, limit);
    }

    public int getDiscussPostCount(Integer userId) {
        return discussPostDao.findDiscussPostCount(userId);
    }
}
