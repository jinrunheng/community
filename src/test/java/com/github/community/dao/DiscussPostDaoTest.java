package com.github.community.dao;

import com.github.community.CommunityApplication;
import com.github.community.entity.DiscussPost;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class DiscussPostDaoTest {
    @Autowired
    private DiscussPostDao discussPostDao;

    @Test
    public void testDiscussPost() {
        List<DiscussPost> discussPosts = discussPostDao.findDiscussPosts(null, 0, 10,0);
        Assertions.assertEquals(discussPosts.size(), 10);
    }


}