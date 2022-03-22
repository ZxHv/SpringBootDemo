## 使用 MyBatis 访问 MySQL
+ 整合 MyBatis
1. 引入依赖
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
```
2. 配置 MySQL 连接以及，配置 Druid 数据库连接池
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
```
当然，druid 也需要引入依赖：
```
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-spring-boot-starter</artifactId>
    <version>1.1.21</version>
</dependency>
```
3. 在 Mysql 中创建一张用来测试的表 USER
```
CREATE TABLE `User` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `age` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
```
4. 创建 USER 表的映射对象 USER.java
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
5. 创建 USER 对象的操作接口：UserMapper
```
@Mapper
public interface UserMapper {

    @Select("SELECT * FROM USER WHERE NAME = #{name}")
    User findByName(@Param("name") String name);

    @Insert("INSERT INTO USER(NAME, AGE) VALUES(#{name}, #{age})")
    int insertUser(@Param("name") String name, @Param("age") Integer age);

    // 通过 Map<String, Object> 对象来作为传递参数的容器
    @Insert("INSERT INTO USER(NAME, AGE) VALUES(#{name,jdbcType=VARCHAR}, #{age,jdbcType=INTEGER})")
    int insertByMap(Map<String, Object> map);

    // 也可直接使用普通的 Java 对象来作为查询条件的传参
    @Insert("INSERT INTO USER(NAME, AGE) VALUES(#{name}, #{age})")
    int insertByUser(User user);

    @Delete("DELETE FROM USER WHERE id = #{id}")
    int deleteById(Long id);

    @Update("UPDATE USER SET AGE = #{age} WHERE NAME = #{name}")
    int updateUser(User user);

    // 对于查询，往往需要多表查询，查询结果可能是一个与数据库实体不同的包装类，可以通过以下方式进行返回结果绑定
    @Results({
            @Result(property = "name", column = "name"),
            @Result(property = "age", column = "age")
    })
    @Select("SELECT NAME, AGE FROM USER")
    List<User> findAll();

}
```
6. 单元测试
```
@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest
public class SelfChapter35ApplicationTests {

    @Autowired
    private UserMapper userMapper;

    //测试结束回滚数据，保证测试单元每次运行的数据环境独立
    @Rollback
    @Test
    public void test() {
        userMapper.insertUser("AAA", 10);
        User user = userMapper.findByName("AAA");
        Assert.assertEquals(10, user.getAge().intValue());

        Map<String, Object> map = new HashMap<>();
        map.put("name", "BBBB");
        map.put("age", 15);
        userMapper.insertByMap(map);

        User user2 = new User();
        user2.setName("CCCC");
        user2.setAge(18);
        userMapper.insertByUser(user2);

        userMapper.deleteById(user.getId());

        user2.setAge(90);
        userMapper.updateUser(user2);

        List<User> userList = userMapper.findAll();
        for (User u : userList) {
            Assert.assertEquals(null, u.getId());
            Assert.assertEquals(null, u.getName());
        }
    }
}
```