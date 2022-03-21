package com.xxexample.selfchapter32;

import com.xxexample.selfchapter32.entity.User;
import com.xxexample.selfchapter32.service.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SelfChapter32ApplicationTests {

    @Autowired
    private UserService userService;

    @Before
    public void setUp(){
        //准备，清空user表
        userService.deleteAllUsers();
    }

    @Test
    public void test() {
        // 插入5个用户
        userService.create("Tom", 10);
        userService.create("Mike", 11);
        userService.create("xxx", 30);
        userService.create("Oscar", 21);
        userService.create("Linda", 17);

        //查询名为 Oscar 的用户，判断年龄是否匹配
        List<User> userList = userService.getUserByName("Oscar");
        Assert.assertEquals(21, userList.get(0).getAge().intValue());

        //查询数据库，应该有5个用户
        Assert.assertEquals(5, userService.getAllUsers());

        //删除两个用户
        userService.deleteByName("Tom");
        userService.deleteByName("Mike");

        // 查数据库，应该有5个用户
        Assert.assertEquals(3, userService.getAllUsers());
    }
}