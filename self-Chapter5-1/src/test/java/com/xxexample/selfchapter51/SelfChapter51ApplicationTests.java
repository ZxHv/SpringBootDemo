package com.xxexample.selfchapter51;

import com.xxexample.selfchapter51.domain.User;
import com.xxexample.selfchapter51.domain.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SelfChapter51ApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CacheManager cacheManager;

    @Test
    public void test() {
        // 创建1条记录
        userRepository.save(new User("AAA", 10));

        User u1 = userRepository.findUserByName("AAA");
        System.out.println("第一次查询：" + u1.getAge());

        User u2 = userRepository.findUserByName("AAA");
        System.out.println("第二次查询：" + u2.getAge());
    }
}