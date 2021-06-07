package com.github.community.controller.interceptor;

import com.github.community.entity.LoginTicket;
import com.github.community.entity.User;
import com.github.community.service.UserService;
import com.github.community.util.CookieUtil;
import com.github.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Objects;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 从 Cookie 中获取 ticket
        String ticket = CookieUtil.getValue(request, "ticket");
        if (!Objects.isNull(ticket)) {
            // 查询凭证
            LoginTicket loginTicket = userService.getLoginTicket(ticket);
            // 判断凭证是否有效
            // loginTicket 不为空
            // &&
            // loginTicket 的状态为 0 （表示有效）
            // &&
            // loginTicket 的超时时间要晚于当前时间
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {
                // 根据凭证查询用户
                User user = userService.getUserById(loginTicket.getUserId());
                // 让本次请求持有用户
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    /**
     * 在模版之前调用
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
