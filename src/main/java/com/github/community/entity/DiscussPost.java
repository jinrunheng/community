package com.github.community.entity;

import lombok.*;

import java.util.Date;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class DiscussPost {
    private Integer id;
    private Integer userId;
    private String title;
    private String content;
    private Integer type;// 0-普通 1-置顶
    private Integer status;// 0-正常,1-精华,2-拉黑
    private Date createTime;
    private Integer commentCount;
    private Double score;
}
