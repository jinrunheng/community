package com.github.community.service;

import com.github.community.dao.UserDao;
import com.github.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    public User getUserById(Integer id) {
        return userDao.findUserById(id);
    }

    public User getUserByName(String username) {
        return userDao.findUserByName(username);
    }
}
