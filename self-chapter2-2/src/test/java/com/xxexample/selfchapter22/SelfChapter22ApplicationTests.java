package com.xxexample.selfchapter22;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@WebAppConfiguration
public class SelfChapter22ApplicationTests {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        //初始化MockMvc对象
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testUserController() throws Exception {
        //测试UserController
        RequestBuilder request;

        // 1、get查一下user列表，应该为空
        request = get("/users/");

        mvc.perform(request)
                .andExpect(status().isOk())  //返回的状态是200
                .andExpect(content().string(equalTo("[]")));

        // 2、post提交一个user
        request = post("/users/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":1,\"name\":\"用户1\",\"age\":19}");
        mvc.perform(request).andExpect(content().string(equalTo("success")));

        // 3、get获取user列表，应该有刚才插入的数据
        request = get("/users/");
        mvc.perform(request)
                .andExpect(status().isOk())  //返回的状态是200
                .andExpect(content().string(equalTo("[{\"id\":1,\"name\":\"用户1\",\"age\":19}]")));

        // 4、put修改id为1的user
        request = put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"用户1\",\"age\":20}");
        mvc.perform(request).andExpect(content().string(equalTo("success")));

        // 5、get一个id为1的user
        request = get("/users/1");
        mvc.perform(request)
                .andExpect(status().isOk())  //返回的状态是200
                .andExpect(content().string(equalTo("{\"id\":1,\"name\":\"用户1\",\"age\":20}")));

        // 6、del删除id为1的user
        request = delete("/users/1");
        mvc.perform(request).andExpect(content().string(equalTo("success")));

        // 7、重新get获取user列表，应该为空
        request = get("/users/");
        mvc.perform(request)
                .andExpect(status().isOk())  //返回的状态是200
                .andExpect(content().string(equalTo("[]")));

    }
}
