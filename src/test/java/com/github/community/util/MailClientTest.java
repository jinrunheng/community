package com.github.community.util;

import com.github.community.CommunityApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailClientTest {
    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testMailClientSendMail() {
        mailClient.sendMail("1175088275@qq.com", "TEST", "this is a test email");
    }

    @Test
    public void testTemplateEngineSendHtmlMail() {
        Context context = new Context();
        context.setVariable("username", "kim");
        String content = templateEngine.process("/mail/demo", context);
        mailClient.sendMail("1175088275@qq.com", "TEST", content);

    }

}