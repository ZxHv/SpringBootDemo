## 使用Swagger3构建强大的API文档
+ Swagger3 能配合 Spring MVC 组织出强大的 RESTful API 文档
+ 能减少创建维护文档的工作量

### 集成 Swagger3 
1.在 pom.xml 中加入依赖
```
<!-- https://mvnrepository.com/artifact/io.springfox/springfox-boot-starter -->
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-boot-starter</artifactId>
    <version>3.0.0</version>
</dependency>
```
此处较 Swagger2.x 旧版本的引入更简单，Swagger2.x 旧版本引入时需要同时引入两个依赖，较为麻烦：
```
<!-- https://mvnrepository.com/artifact/io.springfox/springfox-swagger2 -->
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>2.9.2</version>
</dependency>

<!-- https://mvnrepository.com/artifact/io.springfox/springfox-swagger-ui -->
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>2.9.2</version>
</dependency>
```

2.给 实体类 添加相关注解，此处与最后访问页面的内容有关，如：
```
@Data
@ApiModel(description="用户实体")
public class User {
    @ApiModelProperty("用户编号")
    private Long id;
    @ApiModelProperty("用户姓名")
    private String name;
    @ApiModelProperty("用户年龄")
    private Integer age;
}
```

3.给controller添加相关注解，此处与最后访问页面呈现的内容有关，如：
```
@Api(tags = "用户管理")
@RestController
@RequestMapping(value = "/users")
public class UserController {}
```
```
@PutMapping("/{id}")
@ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "用户编号", required = true, example = "1")
@ApiOperation(value = "更新用户详细信息", notes = "根据url的id来指定更新对象，并根据传过来的user信息来更新用户详细信息")
public String putUser(@PathVariable Long id, @RequestBody User user){
    User user1 = users.get(id);
    user1.setName(user.getName());
    user1.setAge(user.getAge());
    users.put(id, user1);
    return "success";
}
```
4. 配置 swagger 摘要信息,3.x版本的配置是 OAS_30 而2.x旧版本的配置是 SWAGGER_2。
```
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30) // v2 不同
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.xxexample.selfchapter22.web")) // 设置扫描路径
                .build();
    }
}
```

5. 在 application.properties 文件中添加配置
```
springboot2.6.x之后将spring MVC默认路径匹配策略从ANT_PATH_MATCHER模式改为PATH_PATTERN_PARSER模式导致出错
解决方法是切换回原先的ANT_PATH_MATCHER模式
spring.mvc.pathmatch.matching-strategy=ant_path_matcher
```

6. 访问 Swagger
```
3.x版本访问地址
http://localhost:8080/swagger-ui/
```
```
2.x旧版本访问地址
http://localhost:8080/swagger-ui.html
```