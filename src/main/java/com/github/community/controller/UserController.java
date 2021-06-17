package com.github.community.controller;

import com.github.community.annotation.LoginRequired;
import com.github.community.entity.User;
import com.github.community.service.LikeService;
import com.github.community.service.UserService;
import com.github.community.util.HostHolder;
import com.github.community.util.MyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping(path = "/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;


    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @GetMapping("/setting")
    @LoginRequired
    public String getUserSettingPage() {
        return "/site/setting";
    }

    // 上传头像步骤
    // 通过 MultipartFile 处理上传文件
    // 对上传的图片进行格式判断，如果不符合要求，则丢出 RuntimeException 异常
    // 对上传的图片进行重命名
    @LoginRequired
    @PostMapping("/upload")
    public String upLoadUserAvatar(MultipartFile multipartFile, Model model) {
        if (multipartFile == null) {
            model.addAttribute("error", "您还没有选择图片");
            return "/site/setting";
        }
        String originalFilename = multipartFile.getOriginalFilename();
        String suffix = MyUtil.getFileSuffix(originalFilename);
        if (Objects.isNull(suffix) || !MyUtil.isImg(suffix)) {
            model.addAttribute("error", "文件格式不正确");
            return "/site/setting";
        }
        String fileName = MyUtil.generateUUID() + "." + suffix;
        // 确定图片在服务器存放的路径
        File dest = new File(uploadPath + "/" + fileName);
        try {
            multipartFile.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败" + e.getMessage());
            throw new RuntimeException(e);
        }
        // 更新当前用户的头像路径
        // http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateUserHeader(user.getId(), headerUrl);
        return "redirect:/index";
    }

    @GetMapping("/header/{fileName}")
    public void getUserAvatar(@PathVariable String fileName, HttpServletResponse response) {
        // 服务器图片的存放路径
        String imgPathOnServer = uploadPath + "/" + fileName;
        // 向浏览器输出图片
        String suffix = MyUtil.getFileSuffix(fileName);
        response.setContentType("image/" + suffix);
        try (
                FileInputStream fis = new FileInputStream(imgPathOnServer);
                OutputStream os = response.getOutputStream()
        ) {
            BufferedOutputStream bos = new BufferedOutputStream(os);
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, b);
            }

        } catch (IOException e) {
            logger.error("读取头像失败" + e.getMessage());
        }
    }

    @LoginRequired
    @PostMapping("/update")
    public String updatePassword(String originalPassword, String newPassword, String confirmPassword, Model model) {
        User user = hostHolder.getUser();
        Map<String, Object> map = userService.updatePassword(user.getId(), originalPassword, newPassword, confirmPassword);
        if (map == null || map.isEmpty()) {
            return "redirect:/logout";
        } else {
            model.addAttribute("originalPasswordMsg", map.get("originalPasswordMsg"));
            model.addAttribute("newPasswordMsg", map.get("newPasswordMsg"));
            model.addAttribute("confirmPasswordMsg", map.get("confirmPasswordMsg"));
            return "/site/setting";
        }

    }

    // 个人主页
    @GetMapping("/profile/{userId}")
    public String getProfilePage(@PathVariable Integer userId, Model model) {
        User user = userService.getUserById(userId);
        if (Objects.isNull(user)) {
            throw new RuntimeException("用户不存在");
        }
        //用户
        model.addAttribute("user", user);
        //用户获得的点赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);
        return "/site/profile";
    }

}
