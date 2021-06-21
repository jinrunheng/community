package com.github.community.controller.interceptor;

import com.github.community.entity.User;
import com.github.community.service.MessageService;
import com.github.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MessageInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private MessageService messageService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user != null && modelAndView != null){
            int unreadLetterCount = messageService.findUnreadLetterCount(user.getId(),null);
            int unreadNoticeCount = messageService.findUnreadNoticeCount(user.getId(),null);
            int unreadTotal = unreadLetterCount + unreadNoticeCount;
            modelAndView.addObject("unreadTotal",unreadTotal);
        }
    }
}
