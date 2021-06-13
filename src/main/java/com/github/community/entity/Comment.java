package com.github.community.entity;

import lombok.*;

import java.util.Date;

/**
 * 评论
 * <p>
 * entityType: 评论的类型；给用户的评论，给帖子的评论，给帖子下的回复的评论...
 * entityType = 1 代表给帖子的评论
 * <p>
 * entityId:
 * 例如 entityType 为 帖子，entityId 为 228 代表的含义就是 给帖子Id为228的评论
 * <p>
 * targetId:
 * 指向的人，给谁发布的评论
 */
@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    private int id;
    private int userId;
    private int entityType;
    private int entityId;
    private int targetId;
    private String content;
    private int status;
    private Date createTime;
}
