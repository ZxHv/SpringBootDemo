package com.xxexample.selfchapter37;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SelfChapter37ApplicationTests {

    @Autowired
    private JdbcTemplate primaryJdbcTemplate;

    @Autowired
    private JdbcTemplate secondaryJdbcTemplate;

    @Before
    public void setUp(){
        primaryJdbcTemplate.update("DELETE FROM USER");
        secondaryJdbcTemplate.update("DELETE FROM USER");
    }

    @Test
    public void test() {
        // 往第一个数据源中插入2条数据
        primaryJdbcTemplate.update("INSERT INTO USER(NAME, AGE) VALUES(?, ?)", "aaa", 20);
        primaryJdbcTemplate.update("INSERT INTO USER(NAME, AGE) VALUES(?, ?)", "bbb", 30);

        // 往第二个数据源中插入 1 条数据，若插入的是第一个数据源，则会主键冲突报错
        secondaryJdbcTemplate.update("INSERT INTO USER(NAME, AGE) VALUES(?, ?)", "ccc", 20);

        // 查一下第一个数据源中是否有 2 条数据，验证插入是否成功
        Assert.assertEquals("2", primaryJdbcTemplate.queryForObject("select count(1) from user", String.class));

        Assert.assertEquals("1", secondaryJdbcTemplate.queryForObject("select count(1) from user", String.class));
    }

}
