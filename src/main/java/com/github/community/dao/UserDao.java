package com.github.community.dao;

import com.github.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao {
    User findUserById(Integer id);

    User findUserByName(String username);

    User findUserByEmail(String email);

    int insertUser(User user);

    int updateUserStatus(Integer id, Integer status);

    int updateUserHeaderUrl(Integer id, String headerUrl);

    int updateUserPassword(Integer id, String password);

    int deleteUserById(Integer id);

    int countUser();
}
