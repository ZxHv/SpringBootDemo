package com.xxexample.selfchapter28.web;

import com.xxexample.selfchapter28.entity.User;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {

    @PostMapping(value = "/user",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public User createUser(@RequestBody User user){
        user.setName("xxspace.com : " + user.getName());
        user.setAge(user.getAge() + 100);

        return user;
    }
}
