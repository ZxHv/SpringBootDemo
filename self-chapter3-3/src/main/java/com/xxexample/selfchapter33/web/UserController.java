package com.xxexample.selfchapter33.web;

import com.xxexample.selfchapter33.entity.User;
import com.xxexample.selfchapter33.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Data
@AllArgsConstructor
@RestController
public class UserController {

    private UserService userService;

    @PostMapping("/user")
    public int create(@RequestBody User user){
        return userService.create(user.getName(), user.getAge());
    }

    @GetMapping("/user/{name}")
    public List<User> getByName(@PathVariable String name){
        return userService.getUserByName(name);
    }

    @DeleteMapping("user/{name}")
    public int deleteByName(@PathVariable String name){
        return userService.deleteByName(name);
    }

    @GetMapping("/user/count")
    public int getAllUsers(){
        return userService.getAllUsers();
    }

    @DeleteMapping("/user/all")
    public int deleteAllUsers(){
        return userService.deleteAllUsers();
    }
}