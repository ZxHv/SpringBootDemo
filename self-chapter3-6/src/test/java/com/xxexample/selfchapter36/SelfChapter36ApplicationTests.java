package com.xxexample.selfchapter36;

import com.xxexample.selfchapter36.domain.User;
import com.xxexample.selfchapter36.mapper.UserMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest
public class SelfChapter36ApplicationTests {

    @Autowired(required = false)
    private UserMapper userMapper;

    @Test
    @Rollback
    public void test() {
        userMapper.insert("AAAA", 15);
        User u = userMapper.findByName("AAAA");
        Assert.assertEquals(15, u.getAge().intValue());
    }

}
