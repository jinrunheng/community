package com.github.community.entity;

import lombok.*;

import java.util.Date;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LoginTicket {
    private Integer id;
    private Integer userId;
    private String ticket;
    // status:0表示有效，1表示无效
    private Integer status;
    private Date expired;
}
