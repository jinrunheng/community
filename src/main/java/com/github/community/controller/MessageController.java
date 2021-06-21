package com.github.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.community.entity.Message;
import com.github.community.entity.Page;
import com.github.community.entity.User;
import com.github.community.service.MessageService;
import com.github.community.service.UserService;
import com.github.community.util.Constant;
import com.github.community.util.HostHolder;
import com.github.community.util.MyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@Controller
public class MessageController implements Constant {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    // 私信列表
    @GetMapping("/letter/list")
    public String getLetterList(Model model, Page page) {
        User user = hostHolder.getUser();
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));
        // 会话列表
        List<Message> conversationList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversations = new ArrayList<>();
        if (conversationList != null) {
            for (Message message : conversationList) {
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", message);
                map.put("letterCount", messageService.findLetterCount(message.getConversationId()));
                map.put("unreadCount", messageService.findUnreadLetterCount(user.getId(), message.getConversationId()));
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target", userService.getUserById(targetId));
                conversations.add(map);
            }
        }
        model.addAttribute("conversations", conversations);
        // 查询未读私信总数
        int unreadLetterCount = messageService.findUnreadLetterCount(user.getId(), null);
        model.addAttribute("unreadLetterCount", unreadLetterCount);
        // 查询未读通知总数
        int unreadNoticeCount = messageService.findUnreadNoticeCount(user.getId(),null);
        model.addAttribute("unreadNoticeCount",unreadNoticeCount);
        return "/site/letter";
    }

    @GetMapping("/letter/detail/{conversationId}")
    public String getLetterDetail(@PathVariable String conversationId, Page page, Model model) {
        // 分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        // 私信列表
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.getUserById(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);
        model.addAttribute("target", getLetterTarget(conversationId));
        // 设置已读
        List<Integer> ids = getUnreadLetterIds(letterList);
        if (!ids.isEmpty()) {
            messageService.updateMessageStatusToRead(ids);
        }
        return "/site/letter-detail";

    }

    /**
     * 获取未读的消息的 id 列表
     *
     * @param letterList
     * @return
     */
    private List<Integer> getUnreadLetterIds(List<Message> letterList) {
        List<Integer> ids = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                // 如果当前登陆的用户是收件人，并且消息的状态为未读时
                if (hostHolder.getUser().getId() == message.getToId() && message.getStatus() == MESSAGE_STATUS_UNREAD) {
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }

    private User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int id_0 = Integer.parseInt(ids[0]);
        int id_1 = Integer.parseInt(ids[1]);
        if (hostHolder.getUser().getId() == id_0) {
            return userService.getUserById(id_1);
        } else {
            return userService.getUserById(id_0);
        }

    }

    @ResponseBody
    @PostMapping("/letter/send")
    public String sendLetter(String toName, String content) {
        User target = userService.getUserByName(toName);
        if (target == null) {
            return MyUtil.getJSONString(1, "目标用户不存在");
        }
        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());

        messageService.addMessage(message);

        return MyUtil.getJSONString(0);
    }

    @PostMapping("/letter/delete")
    @ResponseBody
    public String deleteLetter(int id) {
        Message letterById = messageService.findLetterById(id);
        if (Objects.isNull(letterById)) {
            return MyUtil.getJSONString(1, "没有找到该私信");
        }
        messageService.updateMessageStatusToDelete(id);
        return MyUtil.getJSONString(0);
    }

    @GetMapping("/notice/list")
    public String getNoticeList(Model model){
        User user = hostHolder.getUser();
        // 查询评论通知
        Message message = messageService.findLatestNoticeByTopic(user.getId(), Constant.TOPIC_COMMENT);
        if(message != null){
            Map<String,Object> messageVO = new HashMap<>();
            messageVO.put("message",message);
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String,Object> data = JSONObject.parseObject(content,HashMap.class);
            messageVO.put("user",userService.getUserById((int)data.get("userId")));
            messageVO.put("entityType",data.get("entityType"));
            messageVO.put("entityId",data.get("entityId"));
            messageVO.put("postId",data.get("postId"));
            int count = messageService.findNoticeCountByTopic(user.getId(),Constant.TOPIC_COMMENT);
            messageVO.put("noticeCount",count);
            int unreadNoticeCount = messageService.findUnreadNoticeCount(user.getId(),Constant.TOPIC_COMMENT);
            messageVO.put("unreadNoticeCount",unreadNoticeCount);
            model.addAttribute("commentNotice",messageVO);
        }

        // 查询赞通知
        message = messageService.findLatestNoticeByTopic(user.getId(),Constant.TOPIC_LIKE);
        if(message != null){
            Map<String,Object> messageVO = new HashMap<>();
            messageVO.put("message",message);
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String,Object> data = JSONObject.parseObject(content,HashMap.class);
            messageVO.put("user",userService.getUserById((int)data.get("userId")));
            messageVO.put("entityType",data.get("entityType"));
            messageVO.put("entityId",data.get("entityId"));
            messageVO.put("postId",data.get("postId"));
            int count = messageService.findNoticeCountByTopic(user.getId(),Constant.TOPIC_LIKE);
            messageVO.put("noticeCount",count);
            int unreadNoticeCount = messageService.findUnreadNoticeCount(user.getId(),Constant.TOPIC_LIKE);
            messageVO.put("unreadNoticeCount",unreadNoticeCount);
            model.addAttribute("likeNotice",messageVO);
        }

        // 查询关注通知
        message = messageService.findLatestNoticeByTopic(user.getId(),Constant.TOPIC_FOLLOW);
        if(message != null){
            Map<String,Object> messageVO = new HashMap<>();
            messageVO.put("message",message);
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String,Object> data = JSONObject.parseObject(content,HashMap.class);
            messageVO.put("user",userService.getUserById((int)data.get("userId")));
            messageVO.put("entityType",data.get("entityType"));
            messageVO.put("entityId",data.get("entityId"));
            int count = messageService.findNoticeCountByTopic(user.getId(),Constant.TOPIC_FOLLOW);
            messageVO.put("noticeCount",count);
            int unreadNoticeCount = messageService.findUnreadNoticeCount(user.getId(),Constant.TOPIC_FOLLOW);
            messageVO.put("unreadNoticeCount",unreadNoticeCount);
            model.addAttribute("followNotice",messageVO);
        }

        // 查询未读消息数量
        int unreadLetterCount = messageService.findUnreadLetterCount(user.getId(),null);
        model.addAttribute("unreadLetterCount",unreadLetterCount);
        int unreadNoticeCount = messageService.findUnreadNoticeCount(user.getId(),null);
        model.addAttribute("unreadNoticeCount",unreadNoticeCount);
        return "/site/notice";
    }

    @GetMapping("/notice/detail/{topic}")
    public String getNoticeDetail(@PathVariable("topic") String topic,Page page,Model model){
        User user = hostHolder.getUser();
        page.setPath("/notice/detail/" + topic);
        page.setRows(messageService.findNoticeCountByTopic(user.getId(),topic));
        // 一页显示五条通知
        page.setLimit(5);

        List<Message> noticeList = messageService.findNoticeListByTopic(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String,Object>> notices = new ArrayList<>();
        if(noticeList != null){
            for(Message notice :noticeList){
                Map<String,Object> map = new HashMap<>();
                map.put("notice",notice);
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String,Object> data = JSONObject.parseObject(content,HashMap.class);
                map.put("user",userService.getUserById((int)data.get("userId")));
                map.put("entityType",data.get("entityType"));
                map.put("entityId",data.get("entityId"));
                map.put("postId",data.get("postId"));
                // 通知的作者:系统用户
                map.put("fromUser",userService.getUserById(notice.getFromId()));
                notices.add(map);
            }
        }
        model.addAttribute("notices",notices);
        // 设置为已读
        List<Integer> ids = getUnreadLetterIds(noticeList);
        if (!ids.isEmpty()) {
            messageService.updateMessageStatusToRead(ids);
        }

        return "/site/notice-detail";
    }

}
