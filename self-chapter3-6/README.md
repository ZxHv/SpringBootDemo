## 使用 MyBatis 的 XML 配置方式
1. 引入相关依赖
```
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>2.1.1</version>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.20</version>
</dependency>
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-spring-boot-starter</artifactId>
    <version>1.1.21</version>
</dependency>
```
2. 配置 MySQL 连接与 Druid 数据库连接池，并指定 MyBatis 的 xml 文件的地址

```
spring.datasource.druid.url=jdbc:mysql://localhost:3306/springboot_test
spring.datasource.druid.username=root
spring.datasource.druid.password=root
#spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.druid.driver-class-name=com.mysql.cj.jdbc.Driver

# 初始化时建立物理连接的个数。初始化发生在显示调用init方法，或者第一次getConnection时，默认值为0
spring.datasource.druid.initialSize=10
# 最大连接池数量，默认值为 8
spring.datasource.druid.maxActive=20
#获取连接时最大等待时间，单位毫秒
spring.datasource.druid.maxWait=60000
# 最小连接池数量
spring.datasource.druid.minIdle=1
#有两个含义：
# 1) Destroy线程会检测连接的间隔时间，如果连接空闲时间大于等于minEvictableIdleTimeMillis则关闭物理连接。
# 2) testWhileIdle的判断依据
spring.datasource.druid.timeBetweenEvictionRunsMillis=60000
# 连接保持空闲而不被驱逐的最小时间
spring.datasource.druid.minEvictableIdleTimeMillis=300000
# 建议配置为true，不影响性能，并且保证安全性。默认为 false
spring.datasource.druid.testWhileIdle=true
# 申请连接时执行 validationQuery 检测连接是否有效，做了这个配置会降低性能。默认 true
spring.datasource.druid.testOnBorrow=true
# 归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。默认 false
spring.datasource.druid.testOnReturn=false
# 是否缓存 preparedStatement，也就是 PSCache。PSCache 对支持游标的数据库性能提升巨大，比如说 oracle。在 mysql 下建议关闭。默认 false
spring.datasource.druid.poolPreparedStatements=true

spring.datasource.druid.maxOpenPreparedStatements=20
# 用来检测连接是否有效的 sql，要求是一个查询语句，常用 select ‘x’。如果 validationQuery 为 null，testOnBorrow、testOnReturn、testWhileIdle 都不会起作用
spring.datasource.druid.validationQuery=SELECT 1

spring.datasource.druid.validation-query-timeout=500
# 属性类型是字符串，通过别名的方式配置扩展插件，常用的插件有：监控统计用的 filter:stat 日志用的 filter:log4j 防御 sql 注入的 filter:wall
spring.datasource.druid.filters=stat

spring.datasource.druid.stat-view-servlet.enabled=true
# 访问地址规则
spring.datasource.druid.stat-view-servlet.url-pattern=/druid/*
# 是否允许清空统计数据
spring.datasource.druid.stat-view-servlet.reset-enable=true
# 监控页面的登录账户
spring.datasource.druid.stat-view-servlet.login-username=admin
# 监控页面的登录密码
spring.datasource.druid.stat-view-servlet.login-password=admin

# 指定xml配置的位置：
mybatis.mapper-locations=classpath:mapper/*.xml
```
3. 创建实体类
```
@Data
@NoArgsConstructor
public class User {
    private Long id;
    private String name;
    private Integer age;

    public User(String name, Integer age) {
        this.name = name;
        this.age = age;
    }
}
```
4. 创建数据库表
```
CREATE TABLE `User` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `age` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
```
5. 在应用主类中增加 mapper 的扫描包配置
```
@MapperScan("com.xxexample.selfchapter36.mapper")
@SpringBootApplication
public class SelfChapter36Application {
    public static void main(String[] args) {
        SpringApplication.run(SelfChapter36Application.class, args);
    }
}
```
6. 在指定的 Mapper 包下创建 User 表的 Mapper 定义:
```
public interface UserMapper {
    User findByName(@Param("name") String name);
    int insert(@Param("name") String name, @Param("age") Integer age);
}
```
7. 在resources文件夹下创建 mapper 目录，再创建 User 表的 mapper xml 配置
```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.xxexample.selfchapter36.mapper.UserMapper">
    <select id="findByName" resultType="com.xxexample.selfchapter36.domain.User">
        SELECT * FROM USER WHERE NAME = #{name}
    </select>

    <insert id="insert">
        INSERT INTO USER(NAME, AGE) VALUES(#{name}, #{age})
    </insert>
</mapper>
```
注意：此处的 xml 文件名须与 mapper 类的类名完全一致
8. 单元测试
```
@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest
public class SelfChapter36ApplicationTests {

    @Autowired(required = false)
    private UserMapper userMapper;

    @Test
    @Rollback
    public void test() {
        userMapper.insert("AAAA", 15);
        User u = userMapper.findByName("AAAA");
        Assert.assertEquals(15, u.getAge().intValue());
    }
}
```