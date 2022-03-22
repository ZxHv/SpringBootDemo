package com.xxexample.selfchapter34.web;

import com.xxexample.selfchapter34.domain.User;
import com.xxexample.selfchapter34.domain.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/user")
    public User create(@RequestBody User user){
        return userRepository.save(user);
    }

    @DeleteMapping("user/{name}")
    public void deleteUser(@PathVariable String name){
        userRepository.delete(userRepository.findUser(name));
    }

    @GetMapping("/user/{name}")
    public User getByName(@PathVariable String name){
        return userRepository.findUserByName(name);
    }

    @GetMapping("/user/count")
    public int findAll(){
        return userRepository.findAll().size();
    }
}