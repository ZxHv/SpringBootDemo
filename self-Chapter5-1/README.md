## 进程内缓存的使用与 Cache 注解
+ 随着时间的积累，应用的使用用户不断增加，数据规模也越来越大，往往数据库查询操作会成为影响用户使用体验的瓶颈，此时使用缓存往往是解决这一问题非常好的手段之一。
### 案例实现
1. 引入相关依赖
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
```
2. 新建实体类
```
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private Integer age;

    public User(String name, Integer age) {
        this.name = name;
        this.age = age;
    }
}
```
3. 新建实体对象操作接口
```
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA中,编写接口以后不需要再编写实现类即可实现数据访问
 */
public interface UserRepository extends JpaRepository<User, Long> {

    // jpa 特性1：通过解析方法名创建查询
    User findUserByName(String name);

    // jpa 特性1：通过解析方法名创建查询
    User findUserByNameAndAge(String name, Integer age);

    // jpa 特性2：通过使用 @Query 注解来创建查询，并通过类似“:name”来映射@Param指定的参数
    @Query("from User u where u.name=:name")
    User findUser(@Param("name") String name);
}
```
4. 在 application.properties 文件中配置 MySQL 连接
```
spring.datasource.url=jdbc:mysql://localhost:3306/springboot_test
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.properties.hibernate.hbm2ddl.auto=create-drop
```
5. 开启 hibernate 对 sql 语句的打印
```
spring.jpa.show-sql=true
```
6. 单元测试
```
@RunWith(SpringRunner.class)
@SpringBootTest
public class SelfChapter51ApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CacheManager cacheManager;

    @Test
    public void test() {
        // 创建1条记录
        userRepository.save(new User("AAA", 10));

        User u1 = userRepository.findUserByName("AAA");
        System.out.println("第一次查询：" + u1.getAge());

        User u2 = userRepository.findUserByName("AAA");
        System.out.println("第二次查询：" + u2.getAge());
    }
}
```
7. 测试结果
在没有加入缓存之前，先执行一下这个案例，可以看到如下的日志，对 MySQL 数据库执行了两次相同的查询：  
```
Hibernate: select user0_.id as id1_0_, user0_.age as age2_0_, user0_.name as name3_0_ from user user0_ where user0_.name=?
第一次查询：10
Hibernate: select user0_.id as id1_0_, user0_.age as age2_0_, user0_.name as name3_0_ from user user0_ where user0_.name=?
第二次查询：10
```
### 引入缓存
1. 引入 cache 依赖
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```
2. 在 Spring Boot 主类中增加 @EnableCaching 注解开启缓存功能
```
@EnableCaching
@SpringBootApplication
public class SelfChapter51Application {
    public static void main(String[] args) {
        SpringApplication.run(SelfChapter51Application.class, args);
    }
}
```
3. 在数据访问接口中，增加缓存配置注解
```
/**
 * Spring Data JPA中,编写接口以后不需要再编写实现类即可实现数据访问
 */
@CacheConfig(cacheNames = "users")
public interface UserRepository extends JpaRepository<User, Long> {

    // jpa 特性1：通过解析方法名创建查询
    @Cacheable
    User findUserByName(String name);

    // jpa 特性1：通过解析方法名创建查询
    User findUserByNameAndAge(String name, Integer age);

    // jpa 特性2：通过使用 @Query 注解来创建查询，并通过类似“:name”来映射@Param指定的参数
    @Query("from User u where u.name=:name")
    User findUser(@Param("name") String name);
}
```
4. 再次执行单元测试，console 窗口输出以下内容
```
Hibernate: insert into user (age, name, id) values (?, ?, ?)
Hibernate: select user0_.id as id1_0_, user0_.age as age2_0_, user0_.name as name3_0_ from user user0_ where user0_.name=?
第一次查询：10
第二次查询：10
```
可以看到此时此处 hibernate 只对 MySQL 数据库执行了一次 Select 操作

### Cache 配置注解
+ @EnableCaching：开启缓存功能
+ @CacheConfig(cacheNames = "users")：配置了该数据访问对象中返回的内容将存储于名为users的缓存对象中
+ @Cacheable：配置了 findUserByName 函数的返回值将被加入缓存。同时在查询时，会先从缓存中获取，若不存在才再发起对数据库的访问