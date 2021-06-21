package com.github.community.service;

import com.duby.util.TrieFilter.TrieFilter;
import com.github.community.dao.MessageDao;
import com.github.community.entity.Message;
import com.github.community.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService implements Constant {
    @Autowired
    private MessageDao messageDao;

    @Autowired
    private TrieFilter trieFilter;


    public List<Message> findConversations(int userId, int offset, int limit) {
        return messageDao.selectConversations(userId, offset, limit);
    }

    public int findConversationCount(int userId) {
        return messageDao.selectConversationCount(userId);
    }

    public List<Message> findLetters(String conversationId, int offset, int limit) {
        return messageDao.selectLetters(conversationId, offset, limit);
    }

    public int findLetterCount(String conversationId) {
        return messageDao.selectLetterCount(conversationId);
    }

    public int findUnreadLetterCount(int userId, String conversationId) {
        return messageDao.selectUnreadLetterCount(userId, conversationId);
    }

    public int addMessage(Message message) {
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(trieFilter.filter(message.getContent(), '*'));
        return messageDao.insertMessage(message);
    }

    public int updateMessageStatusToRead(List<Integer> ids) {
        return messageDao.updateStatus(ids, MESSAGE_STATUS_READ);
    }

    public int updateMessageStatusToDelete(int id) {
        List<Integer> ids = new ArrayList<>();
        ids.add(id);
        return messageDao.updateStatus(ids, MESSAGE_STATUS_DELETE);
    }

    public Message findLetterById(int id) {
        return messageDao.selectLetterById(id);
    }

    // 获取某个主题下最新的通知
    public Message findLatestNoticeByTopic(int userId,String topic){
        return messageDao.selectLatestNoticeByTopic(userId,topic);
    }

    // 获取某个主题所包含的通知数量
    public int findNoticeCountByTopic(int userId,String topic){
        return messageDao.selectNoticeCountByTopic(userId,topic);
    }

    // 查询未读的通知数量
    // 如果传入 topic，就查询某个主题的未读通知数量
    // 如果 topic 为 null，就查询所有的主题的未读通知数量
    public int findUnreadNoticeCount(int userId,String topic){
        return messageDao.selectUnreadNoticeCount(userId,topic);
    }

    public List<Message> findNoticeListByTopic(int userId,String topic,int offset,int limit){
        return messageDao.selectNoticeListByTopic(userId,topic,offset,limit);
    }
}
