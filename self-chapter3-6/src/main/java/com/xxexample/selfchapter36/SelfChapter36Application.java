package com.xxexample.selfchapter36;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.xxexample.selfchapter36.mapper")
@SpringBootApplication
public class SelfChapter36Application {
    public static void main(String[] args) {
        SpringApplication.run(SelfChapter36Application.class, args);
    }
}
