package com.xxexample.selfchapter13.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

@Slf4j
@RestController
public class HelloController {

    @Value("${db:}")
    private String db;

    @Value("${mq:}")
    private String mq;

    @RequestMapping("/")
    public String index(){
        log.info("db:" + db);
        log.info("mq:" + mq);
        return "";
    }
    
}
