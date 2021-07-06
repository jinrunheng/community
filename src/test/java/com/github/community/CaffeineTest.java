package com.github.community;

import com.github.community.service.DiscussPostService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class CaffeineTest {

    @Autowired
    private DiscussPostService postService;

    @Test
    public void testCache(){
        System.out.println(postService.getDiscussPosts(0,0,10,1));
        System.out.println(postService.getDiscussPosts(0,0,10,1));
        System.out.println(postService.getDiscussPosts(0,0,10,1));
    }
}
