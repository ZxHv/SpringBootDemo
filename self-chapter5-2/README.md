## EhCache 缓存的使用
### Spring Boot 根据下面的顺序去侦测缓存提供者
  + Generic 
  + JCache (JSR-107) (EhCache 3, Hazelcast, Infinispan, and others)
  + EhCache 2.x
  + Hazelcast
  + Infinispan
  + Couchbase
  + Redis
  + Caffeine
  + Simple
    
### 除了按顺序侦测外，也可以通过配置属性 spring.cache.type 来强制指定
### 使用 EhCache
1. 引入 ehcache 的相关依赖
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
<dependency>
    <groupId>net.sf.ehcache</groupId>
    <artifactId>ehcache</artifactId>
</dependency>
```
2. 定义实体类
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
3. 定义实体操作接口
```
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA中,编写接口以后不需要再编写实现类即可实现数据访问
 * 配置了该数据访问对象中返回的内容将存储于名为 users 的缓存对象中
 * 配置了 findUserByName 函数的返回值将被加入缓存。同时在查询时，会先从缓存中获取，若不存在才再发起对数据库的访问
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
4. 在 Spring Boot 的启动类中增加 @EnableCaching 注解开启缓存功能
```
@EnableCaching
@SpringBootApplication
public class SelfChapter52Application {
    public static void main(String[] args) {
        SpringApplication.run(SelfChapter52Application.class, args);
    }
}
```
5. 在 src/main/resources 目录下创建：ehcache.xml
```
<ehcache xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:noNamespaceSchemaLocation="http://ehcache.org/ehcache.xsd">

    <cache name="users"
           maxEntriesLocalHeap="200"
           timeToLiveSeconds="600">
    </cache>
</ehcache>
```
6. 单元测试
```
import com.xxexample.selfchapter52.domain.User;
import com.xxexample.selfchapter52.domain.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SelfChapter52ApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CacheManager cacheManager;

    @Test
    public void test() {

        //观察此时 CacheManager 应是 EhCacheManager 实例,而不是上一篇中的 ConcurrentHashMap 了
        System.out.println("CacheManager type : " + cacheManager.getClass());

        // 创建1条记录
        userRepository.save(new User("AAA", 10));

        User u1 = userRepository.findUserByName("AAA");
        System.out.println("第一次查询：" + u1.getAge());

        User u2 = userRepository.findUserByName("AAA");
        System.out.println("第二次查询：" + u2.getAge());
    }
}
```
7. 测试结果,控制台输出
```
CacheManager type : class org.springframework.cache.ehcache.EhCacheCacheManager
Hibernate: select next_val as id_val from hibernate_sequence for update
Hibernate: update hibernate_sequence set next_val= ? where next_val=?
Hibernate: insert into user (age, name, id) values (?, ?, ?)
Hibernate: select user0_.id as id1_0_, user0_.age as age2_0_, user0_.name as name3_0_ from user user0_ where user0_.name=?
第一次查询：10
第二次查询：10
```