package com.github.community.util;

public interface Constant {

    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 0;

    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT = 1;

    /**
     * 激活失败
     */
    int ACTIVATION_FAILURE = 2;

    /**
     * 默认状态登陆凭证超时时间:1天
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /**
     * 勾选 rememberMe 之后登陆凭证超时时间:100天
     */
    int REMEMBER_EXPIRED_SECONDS = 3600 * 12 * 100;

    /**
     * entityType
     * entityType = 1 代表给帖子的评论
     */
    int ENTITY_TYPE_POST = 1;

    /**
     * entityType
     * entityType = 2 代表给评论的评论
     */
    int ENTITY_TYPE_COMMENT = 2;

    /**
     * message status = 0
     * 代表消息未读
     */
    int MESSAGE_STATUS_UNREAD = 0;

    /**
     * message status = 1
     * 代表消息已读
     */
    int MESSAGE_STATUS_READ = 1;

    /**
     * message status = 2
     * 代表消息已经被删除
     */
    int MESSAGE_STATUS_DELETE = 2;

    /**
     * like status yes 表示已经点赞
     */
    int LIKE_STATUS_YES = 1;

    /**
     * like status no 表示没有点赞
     */
    int LIKE_STATUS_NO = 0;

}
