## 使用国产数据库连接池Druid
+ Druid是由阿里巴巴数据库事业部出品的开源项目。它除了是一个高性能数据库连接池之外，更是一个自带监控的数据库连接池
### 配置过程
1. 在 pom.xml 中引入 druid 官方提供的 Spring Boot Starter 封装
```
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-spring-boot-starter</artifactId>
    <version>1.1.21</version>
</dependency>
```
2. 在 application.properties 中配置数据库连接信息。
```
spring.datasource.druid.url=jdbc:mysql://localhost:3306/springboot_test
spring.datasource.druid.username=root
spring.datasource.druid.password=root
#spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.druid.driver-class-name=com.mysql.cj.jdbc.Driver
```
3. 配置 druid 的连接池
```
spring.datasource.druid.initialSize=10
spring.datasource.druid.maxActive=20
spring.datasource.druid.maxWait=60000
spring.datasource.druid.minIdle=1
spring.datasource.druid.timeBetweenEvictionRunsMillis=60000
spring.datasource.druid.minEvictableIdleTimeMillis=300000
spring.datasource.druid.testWhileIdle=true
spring.datasource.druid.testOnBorrow=true
spring.datasource.druid.testOnReturn=false
spring.datasource.druid.poolPreparedStatements=true
spring.datasource.druid.maxOpenPreparedStatements=20
spring.datasource.druid.validationQuery=SELECT 1
spring.datasource.druid.validation-query-timeout=500
spring.datasource.druid.filters=stat
```
4. 配置 druid 监控，在 pom.xml 中引入 spring-boot-starter-actuator 依赖
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```
5. 在 application.properties 中添 加Druid 的监控配置
```
spring.datasource.druid.stat-view-servlet.enabled=true
# 访问地址规则
spring.datasource.druid.stat-view-servlet.url-pattern=/druid/*
# 是否允许清空统计数据
spring.datasource.druid.stat-view-servlet.reset-enable=true
# 监控页面的登录账户
spring.datasource.druid.stat-view-servlet.login-username=admin
# 监控页面的登录密码
spring.datasource.druid.stat-view-servlet.login-password=admin
```
6. 创建一个 UserController 来调用数据访问操作
```
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
```
7. 完成上述配置后，启动应用，并访问 http://localhost:8080/druid/ ，登录即可看到相关监控信息
8. 利用 postman 等工具去调用相关接口后，可以在 SQL监控 下看到该 Spring Boot 应用都执行了哪些 SQL，执行频率与效率等
9. 执行时间、读取行数、更新行数等都通过区间分布的方式标识，将耗时分布成8个区间：
```
0 - 1 耗时0到1毫秒的次数
1 - 10 耗时1到10毫秒的次数
10 - 100 耗时10到100毫秒的次数
100 - 1,000 耗时100到1000毫秒的次数
1,000 - 10,000 耗时1到10秒的次数
10,000 - 100,000 耗时10到100秒的次数
100,000 - 1,000,000 耗时100到1000秒的次数
1,000,000 - 耗时1000秒以上的次数
```
  记录耗时区间的发生次数，通过区间分布，可以看出SQL运行得极好、普通和极差的分布。
10. 注意：这里的所有监控信息都是对此应用实例的数据源而言的，并不是数据库全局层面的，可以视为应用层的监控，不可能作为中间层的监控。