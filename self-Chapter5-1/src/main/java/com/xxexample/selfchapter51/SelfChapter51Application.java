package com.xxexample.selfchapter51;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class SelfChapter51Application {
    public static void main(String[] args) {
        SpringApplication.run(SelfChapter51Application.class, args);
    }
}
