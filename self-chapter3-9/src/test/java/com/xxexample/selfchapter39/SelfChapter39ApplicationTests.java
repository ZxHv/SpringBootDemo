package com.xxexample.selfchapter39;

import com.xxexample.selfchapter39.domain.primary.UserMapperPrimary;
import com.xxexample.selfchapter39.domain.primary.UserPrimary;
import com.xxexample.selfchapter39.domain.secondary.UserMapperSecondary;
import com.xxexample.selfchapter39.domain.secondary.UserSecondary;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SelfChapter39ApplicationTests {

    @Autowired(required = false)
    private UserMapperPrimary userMapperPrimary;
    @Autowired(required = false)
    private UserMapperSecondary userMapperSecondary;

    @Before
    public void setUp() {
        // 清空测试表，保证每次结果一样
        userMapperPrimary.deleteAll();
        userMapperSecondary.deleteAll();
    }

    @Test
    public void test() {
        // 往 Primary 数据源插入一条数据
        userMapperPrimary.insertUser("AAAA", 30);
        // 从Primary数据源查询刚才插入的数据，配置正确就可以查询到
        UserPrimary userPrimary = userMapperPrimary.findByName("AAAA");
        Assert.assertEquals(30, userPrimary.getAge().intValue());
        Assert.assertEquals("AAAA", userPrimary.getName());

        // 从Secondary数据源查询刚才插入的数据，配置正确应该是查询不到的
        UserSecondary userSecondary = userMapperSecondary.findByName("AAAA");
        Assert.assertNull(userSecondary);

        // 往Secondary数据源插入一条数据
        userMapperSecondary.insertUser("BBB", 20);

        // 从Primary数据源查询刚才插入的数据，配置正确应该是查询不到的
        userPrimary = userMapperPrimary.findByName("BBB");
        Assert.assertNull(userPrimary);

        // 从Secondary数据源查询刚才插入的数据，配置正确就可以查询到
        userSecondary = userMapperSecondary.findByName("BBB");
        Assert.assertEquals(20, userSecondary.getAge().intValue());
    }
}