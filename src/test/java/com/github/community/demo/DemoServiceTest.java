package com.github.community.demo;

import com.github.community.CommunityApplication;
import com.github.community.dao.DiscussPostDao;
import com.github.community.dao.UserDao;
import com.github.community.service.DiscussPostService;
import com.github.community.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class DemoServiceTest {

    @Autowired
    private DemoService demoService;


    @Autowired
    private UserDao userDao;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private DiscussPostDao discussPostDao;

    @Test
    public void testSaveByDeclarativeTransaction() {
        int userCount = userDao.countUser();
        int postCount = discussPostDao.findDiscussPostCount(null);
        Assertions.assertThrows(NumberFormatException.class, () -> {
            demoService.saveByDeclarativeTransaction();
        });
        Assertions.assertEquals(userDao.countUser(), userCount);
        Assertions.assertEquals(discussPostDao.findDiscussPostCount(null), postCount);
    }

    @Test
    public void testSaveByProgrammaticTransaction() {
        int userCount = userDao.countUser();
        int postCount = discussPostDao.findDiscussPostCount(null);
        Assertions.assertThrows(NumberFormatException.class, () -> {
            demoService.saveByProgrammaticTransaction();
        });
        Assertions.assertEquals(userDao.countUser(), userCount);
        Assertions.assertEquals(discussPostDao.findDiscussPostCount(null), postCount);
    }
}