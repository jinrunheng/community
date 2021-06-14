package com.github.community.entity;

import lombok.*;

import java.util.Date;

/**
 * 私信会话列表
 * fromId 表示私信是谁发送的，当 fromId = 1 代表系统发送的消息，为系统通知
 * toId 表示私信发给谁
 * conversationId 由 fromId 及 toId 拼接，并且按照自然序排序而成，例如111_112
 * status 表示私信的状态，0表示未读，1表示已读，2表示删除
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class Message {
    private int id;
    private int fromId;
    private int toId;
    private String conversationId;
    private int status;
    private Date createTime;
}
