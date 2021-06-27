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
     * entityType = 1 帖子
     */
    int ENTITY_TYPE_POST = 1;

    /**
     * entityType
     * entityType = 2 给评论的评论
     */
    int ENTITY_TYPE_COMMENT = 2;

    /**
     * entityType
     * entityType = 3 用户
     */
    int ENTITY_TYPE_USER = 3;

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

    /**
     * 主题：评论
     */
    String TOPIC_COMMENT = "comment";

    /**
     * 主题：点赞
     */
    String TOPIC_LIKE = "like";

    /**
     * 主题：关注
     */
    String TOPIC_FOLLOW = "follow";

    /**
     * 主题：发帖
     */
    String TOPIC_PUBLISH = "publish";

    /**
     * 主题：删帖
     */
    String TOPIC_DELETE = "delete";

    /**
     * 系统用户的 ID
     */
    int SYSTEM_USER_ID = 1;

    /**
     * 权限：普通用户
     */
    String AUTHORITY_USER = "user";

    /**
     * 权限：管理员
     */
    String AUTHORITY_ADMIN = "admin";

    /**
     * 权限：版主
     */
    String AUTHORITY_MODERATOR = "moderator";

    /**
     * 帖子类型：0 普通帖子
     */
    int DISCUSS_TYPE_NORMAL = 0;

    /**
     * 帖子类型：1 置顶帖子
     */
    int DISCUSS_TYPE_TOP = 1;

    /**
     * 帖子状态：0 正常
     */
    int DISCUSS_STATUS_NORMAL = 0;

    /**
     * 帖子状态：1 加精
     */
    int DISCUSS_STATUS_WONDERFUL = 1;

    /**
     * 帖子状态：2 删除
     */
    int DISCUSS_STATUS_DELETE = 2;
}
