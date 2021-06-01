package com.github.community;

import com.github.community.controller.HelloController;
import com.github.community.dao.HelloDao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class CommunityApplicationTests implements ApplicationContextAware {

    private ApplicationContext applicationContext;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Test
    public void testApplicationContext() {
        HelloController helloControllerBean = applicationContext.getBean(HelloController.class);
        Assertions.assertEquals("Hello Spring Boot", helloControllerBean.hello());
        HelloDao helloDaoBean = applicationContext.getBean(HelloDao.class);
        Assertions.assertEquals(helloDaoBean.select(), "select success");
    }

    @Test
    public void testConfigurationBean() {
        SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
        System.out.println(simpleDateFormat.format(new Date()));
        ;
    }

    @Autowired
    private HelloDao helloDao;

    @Test
    public void testDI() {
        Assertions.assertEquals(helloDao.select(),"select success");
    }
}
