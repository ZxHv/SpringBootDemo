package com.xxexample.selfchapter22.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * 用@Configuration注解此类，等价于XML中配置beans；用@Bean标注方法等价于XML中配置bean。
 */
@Configuration
@EnableOpenApi
public class SwaggerConfig {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30) // v2 不同
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.xxexample.selfchapter22.web")) // 设置扫描路径
                .build();
    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("使用Swagger3构建API文档")
                .description("简单优雅的restful风格")
                .termsOfServiceUrl("http://xxx.com")
                .version("1.0")
                .build();
    }

}
