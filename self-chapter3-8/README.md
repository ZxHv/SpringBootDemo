## Spring Data JPA 的多数据源配置
+ 配置示例
1. 引入相关依赖
```
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```
2. 在 application.properties 中配置两个数据源
```
spring.datasource.primary.jdbc-url=jdbc:mysql://localhost:3306/springboot_test
spring.datasource.primary.username=root
spring.datasource.primary.password=root
spring.datasource.primary.driver-class-name=com.mysql.cj.jdbc.Driver

spring.datasource.secondary.jdbc-url=jdbc:mysql://localhost:3306/springboot_test2
spring.datasource.secondary.username=root
spring.datasource.secondary.password=root
spring.datasource.secondary.driver-class-name=com.mysql.cj.jdbc.Driver

# 日志打印执行的SQL
spring.jpa.show-sql=true
# Hibernate的DDL策略
#spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.hibernate.ddl-auto=update
```
3. 初始化数据源与 JPA 配置
```
@Configuration
public class DataSourceConfiguration {

    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    public DataSource primaryDataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.secondary")
    public DataSource secondaryDataSource(){
        return DataSourceBuilder.create().build();
    }
}
```
4. Primary 数据源的 JPA 配置
```
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactoryPrimary",
        transactionManagerRef = "transactionManagerPrimary",
        basePackages = "com.xxexample.selfchapter38.configuration.primary") //设置 Repository 所在位置
public class PrimaryJPAConfig {

    @Autowired
    @Qualifier("primaryDataSource")
    private DataSource primaryDataSource;

    @Autowired
    private JpaProperties jpaProperties;

    @Autowired
    private HibernateProperties hibernateProperties;

    private Map<String, Object> getVendorProperties(){
        return hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings());
    }

    @Primary
    @Bean(name = "entityManagerPrimary")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return entityManagerFactoryPrimary(builder).getObject().createEntityManager();
    }

    @Primary
    @Bean(name = "entityManagerFactoryPrimary")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryPrimary (EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(primaryDataSource)
                .packages("com.xxexample.selfchapter38.configuration.primary") //设置实体类所在位置
                .persistenceUnit("primaryPersistenceUnit")
                .properties(getVendorProperties())
                .build();
    }

    @Primary
    @Bean(name = "transactionManagerPrimary")
    public PlatformTransactionManager transactionManagerPrimary(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManagerFactoryPrimary(builder).getObject());
    }
}
```
5. Secondary 数据源的 JPA 配置, 基本与 Primary 的配置相同
```
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactorySecondary",
        transactionManagerRef = "transactionManagerSecondary",
        basePackages = "com.xxexample.selfchapter38.configuration.secondary")////设置Repository所在位置
public class SecondaryJPAConfig {

    @Autowired
    @Qualifier("secondaryDataSource")
    private DataSource secondaryDataSource;

    @Autowired
    private JpaProperties jpaProperties;

    @Autowired
    private HibernateProperties hibernateProperties;

    private Map<String, Object> getVendorProperties(){
        return hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings());
    }

    @Bean(name = "entityManagerSecondary")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return entityManagerFactorySecondary(builder).getObject().createEntityManager();
    }

    @Bean(name = "entityManagerFactorySecondary")
    public LocalContainerEntityManagerFactoryBean entityManagerFactorySecondary (EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(secondaryDataSource)
                .packages("com.xxexample.selfchapter38.configuration.secondary") //设置实体类所在位置
                .persistenceUnit("secondaryPersistenceUnit")
                .properties(getVendorProperties())
                .build();
    }

    @Bean(name = "transactionManagerSecondary")
    public PlatformTransactionManager transactionManagerSecondary(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManagerFactorySecondary(builder).getObject());
    }
}
```
6. 创建实体类与对应的 Repository 接口，注意存放的位置须与上述数据源的JPA的配置对应
```
package com.xxexample.selfchapter38.configuration.primary;

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
```
package com.xxexample.selfchapter38.configuration.primary;

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
```
package com.xxexample.selfchapter38.configuration.secondary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
public class Message {

    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private String message;

    public Message(String title, String message) {
        this.title = title;
        this.message = message;
    }
}
```
```
package com.xxexample.selfchapter38.configuration.secondary;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

}
```
7. 单元测试
```
@RunWith(SpringRunner.class)
@SpringBootTest
public class SelfChapter38ApplicationTests {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageRepository messageRepository;

    @Test
    public void test() {
        userRepository.save(new User("aaa", 10));
        userRepository.save(new User("bbb", 20));
        userRepository.save(new User("ccc", 30));
        userRepository.save(new User("ddd", 40));
        userRepository.save(new User("eee", 50));

        Assert.assertEquals(5, userRepository.findAll().size());

        messageRepository.save(new Message("o1", "aaaaaaaaaa"));
        messageRepository.save(new Message("o2", "bbbbbbbbbb"));
        messageRepository.save(new Message("o3", "cccccccccc"));

        Assert.assertEquals(3, messageRepository.findAll().size());
    }
}
```