## 使用 Flyway 管理数据库版本
### Flyway 简介
```
Flyway 是一个简单开源数据库版本控制器（约定大于配置），主要提供 migrate、clean、info、validate、baseline、repair 等命令。它支持 SQL（PL/SQL、T-SQL）方式和 Java 方式，支持命令行客户端等，还提供一系列的插件支持（Maven、Gradle、SBT、ANT等）。
```
### 实现案例
+ 目标1：使用 JdbcTemplate 实现对 USER 表的增删改查操作。
+ 目标2：对用户表新增一个字段，实现对数据库表结构的版本控制。
### 目标1
1. 引入相关依赖
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```
2. 按 Flyway 的规范创建版本化的 SQL 脚本
+ 在工程的 src/main/resources 目录下创建 db 目录，在 db 目录下再创建 migration 目录
+ 在 migration 目录下创建版本化的 SQL 脚本 V1__Base_version.sql
```
DROP TABLE IF EXISTS user ;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(20) NOT NULL COMMENT '姓名',
  `age` int(5) DEFAULT NULL COMMENT '年龄',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```
3. 根据 USER 表的结构，编写对应的实体类
```
@Data
@NoArgsConstructor
public class User {
    private Long id;
    private String name;
    private Integer age;
}
```
4. 编写 User 操作接口和实现
```
public interface UserService {

    /**
     * 新增一个用户
     * @param name
     * @param age
     * @return
     */
    int create(String name, Integer age);

    /**
     * 根据name查询用户
     * @param name
     * @return
     */
    List<User> getByName(String name);

    /**
     * 根据name删除用户
     * @param name
     * @return
     */
    int deleteByName(String name);

    /**
     * 获取用户总量
     * @return
     */
    int getAllUsers();

    /**
     * 删除所有用户
     * @return
     */
    int deleteAllUsers();
}
```
```
@Service
public class UserServiceImpl implements UserService {

    private JdbcTemplate jdbcTemplate;

    public UserServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int create(String name, Integer age) {
        return jdbcTemplate.update("insert into user(NAME, AGE) values(?, ?)", name, age);
    }

    @Override
    public List<User> getByName(String name) {

        String sql = "select * from user where name = ?";

        List<User> users = jdbcTemplate.query(sql, (resultSet, i) -> {
            User user = new User();
            user.setId(resultSet.getLong("ID"));
            user.setName(resultSet.getString("NAME"));
            user.setAge(resultSet.getInt("AGE"));
            return user;
        }, name);

        return users;
    }

    @Override
    public int deleteByName(String name) {
        return jdbcTemplate.update("DELETE FROM USER WHERE NAME = ?", name);
    }

    @Override
    public int getAllUsers() {
        return jdbcTemplate.queryForObject("SELECT count(1) FROM USER", Integer.class);
    }

    @Override
    public int deleteAllUsers() {
        return jdbcTemplate.update("DELETE FROM USER");
    }
}
```
5. 配置 MySQL 的连接信息
```
spring.datasource.url=jdbc:mysql://localhost:3306/springboot_test
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```
6. 编写单元测试
```
@RunWith(SpringRunner.class)
@SpringBootTest
public class SelfChapter311ApplicationTests {

    @Autowired
    private UserService userService;

    @Before
    public void setUp(){
        userService.deleteAllUsers();
    }

    @Test
    public void test() {
        // 插入5个用户
        userService.create("Tom", 10);
        userService.create("Mike", 11);
        userService.create("Didispace", 30);
        userService.create("Oscar", 21);
        userService.create("Linda", 17);

        // 查询名为Oscar的用户，判断年龄是否匹配
        List<User> userList = userService.getByName("Oscar");
        Assertions.assertEquals(21, userList.get(0).getAge().intValue());

        // 查数据库，应该有5个用户
        Assertions.assertEquals(5, userService.getAllUsers());

        // 删除两个用户
        userService.deleteByName("Tom");
        userService.deleteByName("Mike");

        // 查数据库，应该有5个用户
        Assertions.assertEquals(3, userService.getAllUsers());
    }
}
```
7. 验证测试结果
测试结果ok，连接数据库时多了两张表：user、flyway_schema_history  
flyway_schema_history: flyway 的管理表，用来记录在这个数据库上跑过的脚本，以及每个脚本的检查依据

### 目标2
+ 使用 Flyway 之后，对数据库表结构的变更就必须关闭以下途径：
```
1. 直接通过工具登录数据库去修改表结构
2. 已经发布的 sql 脚本不允许修改
```
+ 正确的表结构调整途径：在 Flyway 脚本配置路径下编写新的脚本，启动程序来执行变更，好处：
```
1. 脚本受 Git 版本管理控制，可以方便的找到过去的历史
2. 脚本在程序启动的时候先加载，再提供接口服务，一起完成部署步骤
3. 所有表结构的历史变迁，在管理目录中根据版本号就能很好的追溯
```
#### 实际操作过程
+ 假如此时需要对 USER 表增加一个 address 字段
1. 创建脚本文件 V1_1__alter_table_user.sql，并写入增加 address 列的语句
```
ALTER TABLE `user` ADD COLUMN `address` VARCHAR(20) DEFAULT NULL;
```
脚本文件名的基本规则是：版本号__描述.sql
2. 再次执行单元测试，此时 USER 表会新增一个栏位，并且 flyway_schema_history 表也会新增一条数据