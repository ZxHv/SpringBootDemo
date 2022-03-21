package com.xxexample.selfchapter32.service.impl;

import com.xxexample.selfchapter32.entity.User;
import com.xxexample.selfchapter32.service.UserService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private JdbcTemplate jdbcTemplate;

    public UserServiceImpl(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int create(String name, Integer age) {
        return jdbcTemplate.update("insert into USER(NAME,AGE) VALUES(?,?)", name ,age);
    }

    @Override
    public List<User> getUserByName(String name) {
        List<User> users = jdbcTemplate.query("SELECT NAME,AGE FROM USER WHERE NAME=?", (resultSet, i) -> {
            User user = new User();
            user.setName(resultSet.getString("NAME"));
            user.setAge(resultSet.getInt("AGE"));
            return user;
        }, name);
        return users;
    }

    @Override
    public int deleteByName(String name) {
        return jdbcTemplate.update("DELETE FROM USER WHERE NAME=?", name);
    }

    @Override
    public int getAllUsers() {
        return jdbcTemplate.queryForObject("SELECT COUNT(1) FROM USER", Integer.class);
    }

    @Override
    public int deleteAllUsers() {
        return jdbcTemplate.update("DELETE FROM USER");
    }
}
