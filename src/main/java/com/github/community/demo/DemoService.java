package com.github.community.demo;

import com.github.community.dao.DiscussPostDao;
import com.github.community.dao.UserDao;
import com.github.community.entity.DiscussPost;
import com.github.community.entity.User;
import com.github.community.util.MyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;

@Service
public class DemoService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private DiscussPostDao discussPostDao;

    @Autowired
    private TransactionTemplate template;

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void saveByDeclarativeTransaction() {
        User user = new User();
        user.setUsername("test");
        user.setSalt(MyUtil.generateUUID().substring(0, 5));
        user.setPassword(MyUtil.md5("123" + user.getSalt()));
        user.setEmail("123@qq.com");
        user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
        user.setCreateTime(new Date());

        userDao.insertUser(user);

        DiscussPost discussPost = DiscussPost.builder()
                .userId(user.getId())
                .title("test title")
                .content("test content")
                .createTime(new Date())
                .type(0)
                .status(0)
                .score(0.0)
                .commentCount(0)
                .build();
        discussPostDao.insertDiscussPost(discussPost);

        // NumberFormatException
        Integer.valueOf("abc");
    }

    public void saveByProgrammaticTransaction() {

        template.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        template.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                User user = new User();
                user.setUsername("test");
                user.setSalt(MyUtil.generateUUID().substring(0, 5));
                user.setPassword(MyUtil.md5("123" + user.getSalt()));
                user.setEmail("123@qq.com");
                user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
                user.setCreateTime(new Date());

                userDao.insertUser(user);

                DiscussPost discussPost = DiscussPost.builder()
                        .userId(user.getId())
                        .title("test title")
                        .content("test content")
                        .createTime(new Date())
                        .type(0)
                        .status(0)
                        .score(0.0)
                        .commentCount(0)
                        .build();
                discussPostDao.insertDiscussPost(discussPost);
                // NumberFormatException
                Integer.valueOf("abc");
            }
        });
    }
}