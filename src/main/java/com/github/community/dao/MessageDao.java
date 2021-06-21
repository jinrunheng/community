package com.github.community.dao;

import com.github.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageDao {

    // 查询当前用户的会话列表,针对每个会话只返回一条最新的私信
    List<Message> selectConversations(int userId, int offset, int limit);

    // 查询当前用户的会话数量
    int selectConversationCount(int userId);

    // 查询某个会话所包含的私信
    List<Message> selectLetters(String conversationId, int offset, int limit);

    // 查询某个会话所包含的私信数量
    int selectLetterCount(String conversationId);

    // 查询未读私信的数量
    // 需要查询两种 第一个是所有的未读私信数量
    // 一个是查询某个会话的未读私信数量
    int selectUnreadLetterCount(int userId, String conversationId);

    // 新增一条私信
    int insertMessage(Message message);

    // 更新消息的状态
    int updateStatus(List<Integer> ids, int status);

    Message selectLetterById(int id);

    // 查询某个主题下最新的通知
    Message selectLatestNoticeByTopic(int userId,String topic);
    // 查询某个主题所包含的通知数量
    int selectNoticeCountByTopic(int userId,String topic);
    // 查询未读的通知数量
    // 如果传入 topic，就查询某个主题的未读通知数量
    // 如果 topic 为 null，就查询所有的主题的未读通知数量
    int selectUnreadNoticeCount(int userId,String topic);

    // 查询某个主题所包含的通知列表
    List<Message> selectNoticeListByTopic(int userId,String topic,int offset,int limit);
}
