package com.github.community.dao;

import org.springframework.stereotype.Repository;

@Repository
public class HelloDaoImpl implements HelloDao {
    @Override
    public String select() {
        return "select success";
    }
}
