package com.github.community.service;

import com.github.community.dao.HelloDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HelloService {

    @Autowired
    private HelloDao helloDao;

    public String select() {
        return helloDao.select();
    }
}
