package com.xxexample.selfchapter35;

import com.xxexample.selfchapter35.domain.User;
import com.xxexample.selfchapter35.domain.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest
public class SelfChapter35ApplicationTests {

    @Autowired
    private UserMapper userMapper;

    //测试结束回滚数据，保证测试单元每次运行的数据环境独立
    @Rollback
    @Test
    public void test() {
        userMapper.insertUser("AAA", 10);
        User user = userMapper.findByName("AAA");
        Assert.assertEquals(10, user.getAge().intValue());

        Map<String, Object> map = new HashMap<>();
        map.put("name", "BBBB");
        map.put("age", 15);
        userMapper.insertByMap(map);

        User user2 = new User();
        user2.setName("CCCC");
        user2.setAge(18);
        userMapper.insertByUser(user2);

        userMapper.deleteById(user.getId());

        user2.setAge(90);
        userMapper.updateUser(user2);

        List<User> userList = userMapper.findAll();
        for (User u : userList) {
            Assert.assertEquals(null, u.getId());
            Assert.assertEquals(null, u.getName());
        }
    }
}
