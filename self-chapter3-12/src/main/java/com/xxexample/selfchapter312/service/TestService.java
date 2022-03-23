package com.xxexample.selfchapter312.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestService {

    private JdbcTemplate primaryJdbcTemplate;
    private JdbcTemplate secondaryJdbcTemplate;

    public TestService(JdbcTemplate primaryJdbcTemplate, JdbcTemplate secondaryJdbcTemplate) {
        this.primaryJdbcTemplate = primaryJdbcTemplate;
        this.secondaryJdbcTemplate = secondaryJdbcTemplate;
    }

    /**
     * 此方法正常情况下都会成功
     */
    @Transactional
    public void tx1() {
        // 修改test1库中的数据
        primaryJdbcTemplate.update("update user set age = ? where name = ?", 50, "aaa");
        // 修改test2库中的数据
        secondaryJdbcTemplate.update("update user set age = ? where name = ?", 50, "aaa");
    }

    /**
     * 人为的制造了一个异常，此异常在 primary 数据库的数据执行更新操作以后才产生，可以测试能否在遇到异常时能在 JTA 的帮助下实现回滚
     */
    @Transactional
    public void tx2() {
        // 修改test1库中的数据
        primaryJdbcTemplate.update("update user set age = ? where name = ?", 40, "aaa");
        // 模拟：修改test2库之前抛出异常
        throw new RuntimeException();
    }
}