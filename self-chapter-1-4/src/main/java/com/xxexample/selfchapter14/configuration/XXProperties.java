package com.xxexample.selfchapter14.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "com.xxspace")
public class XXProperties {
    /**
     * 这是一个自定义配置
     */
    private String from;
}
