package com.xxexample.selfchapter51.domain;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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