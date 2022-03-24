package com.xxexample.selfchapter52;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class SelfChapter52Application {
    public static void main(String[] args) {
        SpringApplication.run(SelfChapter52Application.class, args);
    }
}