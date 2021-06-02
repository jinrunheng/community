package com.github.community.dao;

import com.github.community.CommunityApplication;
import com.github.community.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.Objects;


@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class UserDaoTest {

    @Autowired
    private UserDao userDao;

    @Test
    public void testUserDao() {
        // test countUser
        int count = userDao.countUser();
        // test insertUser
        int insert = userDao.insertUser(User.builder()
                .username("testUser123")
                .password("testUser")
                .salt("test")
                .email("test123test@test.com")
                .type(0)
                .status(1)
                .headerUrl("http://testUser.com")
                .createTime(new Date())
                .build());
        Assertions.assertEquals(insert, 1);

        // test findUserByName
        User testUser = userDao.findUserByName("testUser123");
        Assertions.assertEquals(testUser.getType(), 0);
        Assertions.assertEquals(testUser.getStatus(), 1);
        Assertions.assertEquals(testUser.getSalt(), "test");

        // test findUserById
        int id = testUser.getId();
        Assertions.assertEquals("testUser123", userDao.findUserById(id).getUsername());

        // test findUserByEmail
        Assertions.assertEquals("testUser123", userDao.findUserByEmail("test123test@test.com").getUsername());

        // test updateUserStatus
        userDao.updateUserStatus(id, 0);
        Assertions.assertEquals(userDao.findUserByName("testUser123").getStatus(), 0);

        // test updateUserHeaderUrl
        userDao.updateUserHeaderUrl(id, "http://updateTestUser.com");
        Assertions.assertEquals(userDao.findUserByName("testUser123").getHeaderUrl(), "http://updateTestUser.com");

        // test updateUserPassword
        userDao.updateUserPassword(id, "updateUserPassword");
        Assertions.assertEquals("updateUserPassword", userDao.findUserByName("testUser123").getPassword());

        // test deleteUserById
        userDao.deleteUserById(id);
        Assertions.assertTrue(Objects.isNull(userDao.findUserByName("testUser123")));
        Assertions.assertEquals(userDao.countUser(), count);
    }
}