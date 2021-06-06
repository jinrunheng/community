package com.github.community.dao;

import com.github.community.CommunityApplication;
import com.github.community.entity.LoginTicket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class LoginTicketDaoTest {

    @Autowired
    private LoginTicketDao loginTicketDao;

    @Test
    public void testLoginTicket() {
        LoginTicket ticket = LoginTicket.builder()
                .userId(101)
                .ticket("testTicket")
                .status(0)
                .expired(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                .build();

        loginTicketDao.insertLoginTicket(ticket);
        Assertions.assertEquals(101, loginTicketDao.findLoginTicketByTicket("testTicket").getUserId());
        Assertions.assertNotNull(loginTicketDao.findLoginTicketById(ticket.getId()));
        loginTicketDao.updateStatus("testTicket", 1);
        Assertions.assertEquals(loginTicketDao.findLoginTicketByTicket("testTicket").getStatus(), 1);
        Assertions.assertEquals(loginTicketDao.deleteLoginTicket(ticket.getId()), 1);
    }
}