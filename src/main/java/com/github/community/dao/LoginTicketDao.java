package com.github.community.dao;

import com.github.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginTicketDao {

    @Insert({
            "insert into login_ticket (user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket ",
            "where ticket = #{ticket}"
    })
    LoginTicket findLoginTicketByTicket(String ticket);

    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket ",
            "where id = #{id}"
    })
    LoginTicket findLoginTicketById(Integer id);

    @Update({
            "update login_ticket set status=#{status} where ticket=#{ticket}"
    })
    int updateStatus(String ticket, Integer status);

    @Delete({
            "delete from login_ticket where id = #{id}"
    })
    int deleteLoginTicket(Integer id);
}
