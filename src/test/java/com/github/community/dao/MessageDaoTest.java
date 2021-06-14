package com.github.community.dao;

import com.github.community.CommunityApplication;
import com.github.community.entity.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class MessageDaoTest {

    @Autowired
    private MessageDao messageDao;

    // 仅在测试数据库中生效
    @Test
    public void testSelectConversations() {
        List<Message> messages = messageDao.selectConversations(111, 0, 20);
        for (Message message : messages) {
            System.out.println(message);
        }
    }

    // 仅在测试数据库中生效
    @Test
    public void testSelectConversationCount() {
        int count = messageDao.selectConversationCount(111);
        Assertions.assertEquals(14, count);
    }

    // 仅在测试数据库中生效
    @Test
    public void testSelectLetters() {
        List<Message> letters = messageDao.selectLetters("111_112", 0, 10);
        for (Message letter : letters) {
            System.out.println(letter);
        }
    }

    @Test
    public void testSelectLetterCount() {
        int count = messageDao.selectLetterCount("111_112");
        Assertions.assertEquals(count, 8);
    }

    @Test
    public void testSelectUnreadLetterCount() {
        int count = messageDao.selectUnreadLetterCount(131, "111_131");
        Assertions.assertEquals(count, 2);
    }

}