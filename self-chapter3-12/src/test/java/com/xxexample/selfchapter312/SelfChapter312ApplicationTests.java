package com.xxexample.selfchapter312;

import com.xxexample.selfchapter312.service.TestService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SelfChapter312ApplicationTests {

    @Autowired
    private JdbcTemplate primaryJdbcTemplate;
    @Autowired
    private JdbcTemplate secondaryJdbcTemplate;

    @Autowired
    private TestService testService;

    @Test
    public void test1() {
        // 正确更新的情况
        testService.tx1();
        String sql1 = "select age from user where name=?";
        Assert.assertEquals(50, primaryJdbcTemplate.queryForObject(sql1, Integer.class, "aaa").intValue());
        Assert.assertEquals(50, secondaryJdbcTemplate.queryForObject(sql1, Integer.class, "aaa").intValue());
    }

    @Test
    public void test2() {
        // 更新失败的情况
        try {
            testService.tx2();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 部分更新失败，test1中的更新应该回滚
            String sql2 = "select age from user where name=?";
            Assert.assertEquals(50, primaryJdbcTemplate.queryForObject(sql2, Integer.class, "aaa").intValue());
            Assert.assertEquals(50, secondaryJdbcTemplate.queryForObject(sql2, Integer.class, "aaa").intValue());
        }
    }
}