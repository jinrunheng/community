package com.github.community.controller;

import com.github.community.entity.User;
import com.github.community.service.UserService;
import com.github.community.util.Constant;
import com.github.community.util.MyUtil;
import com.github.community.util.RedisKeyGenerator;
import com.google.code.kaptcha.Producer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController implements Constant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/register")
    public String getRegisterPage() {
        return "/site/register";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "/site/login";
    }

    @PostMapping("/register")
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);
        if (Objects.isNull(map) || map.isEmpty()) {
            // 注册成功
            model.addAttribute("msg", "注册成功，我们已经向您邮箱发送了一封邮件，请尽快激活！");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }
    }

    // http://locahost:8080/community/activation/userId/activationCode
    @GetMapping("/activation/{userId}/{activationCode}")
    public String activation(Model model, @PathVariable Integer userId, @PathVariable String activationCode) {
        int result = userService.activation(userId, activationCode);
        if (result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功，您的账号已经可以正常使用！");
            model.addAttribute("target", "/login");
        } else if (result == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "无效操作，该账号已激活过！");
            model.addAttribute("target", "/index");
        } else {
            model.addAttribute("msg", "激活失败，您提供的激活码不正确！");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }

    @GetMapping("/kaptcha")
    public void getKaptcha(HttpServletResponse response/*, HttpSession session*/) {

        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        // 将验证码存入到session
        // 原本将验证码存储到 session 中，现在将验证码存储到 redis 中
        // session.setAttribute("kaptcha", text);

        // 验证码的 owner
        String kaptchaOwner = MyUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);
        // 将验证码存入到 redis
        String redisKey = RedisKeyGenerator.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey, text, 60, TimeUnit.SECONDS);


        // 将图片输出给浏览器
        response.setContentType("image/png");
        try {
            ServletOutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            logger.error("获取验证码失败：" + e.getMessage());
        }
    }

    @PostMapping("/login")
    public String login(String username,
                        String password,
                        String code,
                        boolean rememberMe,
                        Model model/*, HttpSession session*/,
                        HttpServletResponse response,
                        @CookieValue("kaptchaOwner") String kaptchaOwner) {
        // String kaptcha = (String) session.getAttribute("kaptcha");
        // 检查验证码
        String kaptcha = null;
        if (StringUtils.isNotBlank(kaptchaOwner)) {
            String redisKey = RedisKeyGenerator.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
        }
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg", "验证码不正确");
            return "/site/login";
        }
        // 检查账号密码
        int expiredSeconds = rememberMe ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> loginMap = userService.login(username, password, expiredSeconds);
        if (loginMap.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket", loginMap.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        } else {
            model.addAttribute("usernameMsg", loginMap.get("usernameMsg"));
            model.addAttribute("passwordMsg", loginMap.get("passwordMsg"));
            return "/site/login";
        }

    }

    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        SecurityContextHolder.clearContext();
        return "redirect:/login";
    }

    @GetMapping("/forget")
    public String getForgetPage() {
        return "/site/forget";
    }

    /**
     * @param email   用户邮箱
     * @param session session 用来存储验证码 有效时间为 5min
     * @return jsonString
     * <p>
     * success:
     * {code:0}
     * <p>
     * failure:
     * <p>
     * {code:1,msg:用户邮箱不能为空}
     * {code:1,msg:用户邮箱尚未注册}
     */
    @GetMapping("/forget/code")
    @ResponseBody
    public String getVerificationCode(String email, HttpSession session) {
        if (StringUtils.isBlank(email)) {
            return MyUtil.getJSONString(1, "用户邮箱不能为空");
        }
        User user = userService.getUserByEmail(email);
        if (Objects.isNull(user)) {
            return MyUtil.getJSONString(1, "用户邮箱尚未注册");
        }
        String code = userService.forgetPasswordAndSendEmail(email);
        session.setAttribute("code", code);
        session.setMaxInactiveInterval(5 * 60);
        return MyUtil.getJSONString(0);
    }

    @PostMapping("/forget/reset")
    public String resetPassword(String email, String verificationCode, String password, Model model, HttpSession session) {
        String code = (String) session.getAttribute("code");
        if (StringUtils.isBlank(verificationCode) ||
                StringUtils.isBlank(code) ||
                !code.equalsIgnoreCase(verificationCode)
        ) {
            model.addAttribute("codeMsg", "验证码错误");
            return "/site/forget";
        }
        Map<String, Object> map = userService.resetPassword(email, password);
        if (map.containsKey("user")) {
            // update success
            return "redirect:/login";
        } else {
            model.addAttribute("emailMsg", map.get("emailMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/forget";
        }

    }
}
