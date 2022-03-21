## 使用 JdbcTemplate 访问 MySQL 数据库
### 在Spring Boot中使用最基本的数据访问工具：JdbcTemplate
### 实现过程
1. 引入 jdbc 支持
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
```
2. 连接 MySQL 数据源
```
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
```
3. 配置数据源信息
```
spring.datasource.url=jdbc:mysql://localhost:3306/springboot_test
spring.datasource.username=root
spring.datasource.password=root
#spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```
4. 创建数据表 USER
```
CREATE TABLE `User` (
  `name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `age` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
```
5. 编写对应的实体对象
```
@Data
@NoArgsConstructor
public class User {
    private String name;
    private Integer age;
}
```
6. 定义操作 User 对象的抽象接口 UserService
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
     * 根据name来查询用户
     * @param name
     * @return
     */
    List<User> getUserByName(String name);

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
7. 编写实现类，通过 JdbcTemplate 实现 UserService 中定义的数据访问操作
```
@Service
public class UserServiceImpl implements UserService {

    private JdbcTemplate jdbcTemplate;

    public UserServiceImpl(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int create(String name, Integer age) {
        return jdbcTemplate.update("insert into USER(NAME,AGE) VALUES(?,?)", name ,age);
    }

    @Override
    public List<User> getUserByName(String name) {
        List<User> users = jdbcTemplate.query("SELECT NAME,AGE FROM USER WHERE NAME=?", (resultSet, i) -> {
            User user = new User();
            user.setName(resultSet.getString("NAME"));
            user.setAge(resultSet.getInt("AGE"));
            return user;
        }, name);
        return users;
    }

    @Override
    public int deleteByName(String name) {
        return jdbcTemplate.update("DELETE FROM USER WHERE NAME=?", name);
    }

    @Override
    public int getAllUsers() {
        return jdbcTemplate.queryForObject("SELECT COUNT(1) FROM USER", Integer.class);
    }

    @Override
    public int deleteAllUsers() {
        return jdbcTemplate.update("DELETE FROM USER");
    }
}
```
8. 编写单元测试用例
```
@RunWith(SpringRunner.class)
@SpringBootTest
public class SelfChapter31ApplicationTests {

    @Autowired
    private UserService userService;

    @Before
    public void setUp(){
        //准备，清空user表
        userService.deleteAllUsers();
    }

    @Test
    public void test() {
        // 插入5个用户
        userService.create("Tom", 10);
        userService.create("Mike", 11);
        userService.create("xxx", 30);
        userService.create("Oscar", 21);
        userService.create("Linda", 17);

        //查询名为 Oscar 的用户，判断年龄是否匹配
        List<User> userList = userService.getUserByName("Oscar");
        Assert.assertEquals(21, userList.get(0).getAge().intValue());

        //查询数据库，应该有5个用户
        Assert.assertEquals(5, userService.getAllUsers());

        //删除两个用户
        userService.deleteByName("Tom");
        userService.deleteByName("Mike");

        // 查数据库，应该有5个用户
        Assert.assertEquals(3, userService.getAllUsers());
    }
}
```