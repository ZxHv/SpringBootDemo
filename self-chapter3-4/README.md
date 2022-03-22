## 使用 Spring Data JPA 访问 MySQL
+ Spring Data JPA 的出现可以让一个已经很“薄”的 dao 数据访问层变成只是一层接口的编写方式
+ 只需要通过编写一个继承自 JpaRepository 的接口就能完成数据访问
### 示例
1. 引入jpa 的相关依赖
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```
2. 在 application.properties 文件中配置数据库连接信息
```
spring.datasource.druid.url=jdbc:mysql://localhost:3306/springboot_test
spring.datasource.druid.username=root
spring.datasource.druid.password=root
#spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.druid.driver-class-name=com.mysql.cj.jdbc.Driver
# hibernate的配置属性，其主要作用是：自动创建、更新、验证数据库表结构
spring.jpa.properties.hibernate.hbm2ddl.auto=create-drop
```
3. 创建实体
```
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
4. 创建继承自 JpaRepository 的数据访问接口
```
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
5. 单元测试
```
@RunWith(SpringRunner.class)
@SpringBootTest
public class SelfChapter34ApplicationTests {

	@Autowired
	private UserRepository userRepository;

	@Test
	public void test() {

		//创建10条数据
		userRepository.save(new User("AAA", 10));
		userRepository.save(new User("BBB", 20));
		userRepository.save(new User("CCC", 30));
		userRepository.save(new User("DDD", 40));
		userRepository.save(new User("EEE", 50));
		userRepository.save(new User("FFF", 60));
		userRepository.save(new User("GGG", 70));
		userRepository.save(new User("HHH", 80));
		userRepository.save(new User("III", 90));
		userRepository.save(new User("JJJ", 100));

		// 测试findAll, 查询所有记录
		Assert.assertEquals(10, userRepository.findAll().size());

		// 测试findByName, 查询姓名为FFF的User
		Assert.assertEquals(60, userRepository.findUserByName("FFF").getAge().longValue());

		// 测试findUser, 查询姓名为FFF的User
		Assert.assertEquals(60, userRepository.findUser("FFF").getAge().longValue());

		// 测试findByNameAndAge, 查询姓名为FFF并且年龄为60的User
		Assert.assertEquals("FFF", userRepository.findUserByNameAndAge("FFF", 60).getName());

		// 测试删除姓名为AAA的User
		userRepository.delete(userRepository.findUserByName("AAA"));

		// 测试findAll, 查询所有记录, 验证上面的删除是否成功
		Assert.assertEquals(9, userRepository.findAll().size());

	}
}
```
6. 创建 Controller
```
@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/user")
    public User create(@RequestBody User user){
        return userRepository.save(user);
    }

    @DeleteMapping("user/{name}")
    public void deleteUser(@PathVariable String name){
        userRepository.delete(userRepository.findUser(name));
    }

    @GetMapping("/user/{name}")
    public User getByName(@PathVariable String name){
        return userRepository.findUserByName(name);
    }

    @GetMapping("/user/count")
    public int findAll(){
        return userRepository.findAll().size();
    }
}
```
7. 本文对 Spring Data JPA 的使用仅介绍了常见的操作，还有诸如：@Modify 操作、分页排序、原生 SQL 支持以及与 Spring MVC 的结合使用等等