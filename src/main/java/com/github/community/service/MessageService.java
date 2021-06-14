package com.github.community.service;

import com.duby.util.TrieFilter.TrieFilter;
import com.github.community.dao.MessageDao;
import com.github.community.entity.Message;
import com.github.community.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

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
        return messageDao.updateStatus(ids,1);
    }
}
