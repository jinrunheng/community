package com.github.community.service;

import com.github.community.dao.UserDao;
import com.github.community.entity.User;
import com.github.community.util.Constant;
import com.github.community.util.MailClient;
import com.github.community.util.MyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;

@Service
public class UserService implements Constant {

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserDao userDao;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    public User getUserById(Integer id) {
        return userDao.findUserById(id);
    }

    public User getUserByName(String username) {
        return userDao.findUserByName(username);
    }

    /**
     * @param user
     * @return if no any problems,you will get a empty map
     * else{
     * if user is null throw new IllegalArgumentException("参数不能为空")
     * if username is empty,return map ("usernameMsg", "账号不能为空")
     * if username is already exist,return map ("usernameMsg", "该账号已存在")
     * if password is empty,return map ("passwordMsg", "密码不能为空")
     * if email is empty ,return map ("emailMsg", "邮箱不能为空")
     * if email is already exist,return map ("emailMsg", "该邮箱已被注册")
     * }
     */
    public Map<String, Object> register(User user) {

        Map<String, Object> map = new HashMap<>();

        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("参数不能为空");
        }

        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空");
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空");
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空");
        }

        // 验证用户注册名是否已存在
        User u = userDao.findUserByName(user.getUsername());
        if (!Objects.isNull(u)) {
            map.put("usernameMsg", "该账号已存在");
            return map;
        }

        // 验证邮箱
        u = userDao.findUserByEmail(user.getEmail());
        if (!Objects.isNull(u)) {
            map.put("emailMsg", "该邮箱已被注册");
            return map;
        }

        // 注册用户
        user.setSalt(MyUtil.generateUUID().substring(0, 5));
        user.setPassword(MyUtil.md5(user.getPassword()) + user.getSalt());
        user.setType(0); // 普通用户
        user.setStatus(0); // 未激活
        user.setActivationCode(MyUtil.generateUUID());
        int num = new Random().nextInt(1000);
        user.setHeaderUrl("https://images.nowcoder.com/head/" + num + "t.png");
        user.setCreateTime(new Date());
        userDao.insertUser(user);

        // 激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // http://locahost:8080/community/activation/userId/activationCode
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);

        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);

        return map;
    }

    public int activation(Integer userId, String code) {
        User user = userDao.findUserById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            userDao.updateUserStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

}
