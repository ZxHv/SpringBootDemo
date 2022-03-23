## MyBatis 的多数据源配置
### 实现案例
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
```
2. 在 application.properties 文件中添加多数据源配置
```
spring.datasource.primary.jdbc-url=jdbc:mysql://localhost:3306/springboot_test
spring.datasource.primary.username=root
spring.datasource.primary.password=root
spring.datasource.primary.driver-class-name=com.mysql.cj.jdbc.Driver

spring.datasource.secondary.jdbc-url=jdbc:mysql://localhost:3306/springboot_test2
spring.datasource.secondary.username=root
spring.datasource.secondary.password=root
spring.datasource.secondary.driver-class-name=com.mysql.cj.jdbc.Driver
```
3. 定义 Primary 数据源要用到的实体和数据访问对象
```
@Data
@NoArgsConstructor
public class UserPrimary {
    private Long id;
    private String name;
    private Integer age;

    public UserPrimary(String name, Integer age) {
        this.name = name;
        this.age = age;
    }
}
```
```
public interface UserMapperPrimary {

    @Select("SELECT * FROM USER WHERE NAME = #{name}")
    UserPrimary findByName(@Param("name") String name);

    @Insert("INSERT INTO USER(NAME, AGE) VALUES(#{name}, #{age})")
    int insertUser(@Param("name") String name, @Param("age") Integer age);

    @Delete("DELETE FROM USER")
    int deleteAll();
}
```
4. 定义 Secondary 数据源要用的实体和数据访问对象
```
@Data
@NoArgsConstructor
public class UserSecondary {
    private Long id;
    private String name;
    private Integer age;

    public UserSecondary(String name, Integer age) {
        this.name = name;
        this.age = age;
    }
}
```
```
public interface UserMapperSecondary {
    @Select("SELECT * FROM USER WHERE NAME = #{name}")
    UserSecondary findByName(@Param("name") String name);

    @Insert("INSERT INTO USER(NAME, AGE) VALUES(#{name}, #{age})")
    int insertUser(@Param("name") String name, @Param("age") Integer age);

    @Delete("DELETE FROM USER")
    int deleteAll();
}
```
5. 初始化数据源
```
@Configuration
public class DataSourceConfiguration {
    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.secondary")
    public DataSource secondaryDataSource() {
        return DataSourceBuilder.create().build();
    }
}
```
6. 创建 Primary 数据源的 MyBatis 配置
```
@Configuration
@MapperScan(
        basePackages = "com.xxexample.selfchapter39.domain.primary",
        sqlSessionFactoryRef = "sqlSessionFactoryPrimary",
        sqlSessionTemplateRef = "sqlSessionTemplatePrimary")
public class PrimaryConfig {

    private DataSource primaryDatasource;

    public PrimaryConfig(@Qualifier("primaryDataSource") DataSource primaryDatasource) {
        this.primaryDatasource = primaryDatasource;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactoryPrimary() throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(primaryDatasource);
        return bean.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplatePrimary() throws Exception {
        return new SqlSessionTemplate(sqlSessionFactoryPrimary());
    }
}
```
7. 创建 Secondary 数据源的 MyBatis 配置
```
@Configuration
@MapperScan(
        basePackages = "com.xxexample.selfchapter39.domain.secondary",
        sqlSessionFactoryRef = "sqlSessionFactorySecondary",
        sqlSessionTemplateRef = "sqlSessionTemplateSecondary")
public class SecondaryConfig {

    private DataSource secondaryDataSource;

    public SecondaryConfig(@Qualifier("secondaryDataSource") DataSource secondaryDataSource) {
        this.secondaryDataSource = secondaryDataSource;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactorySecondary() throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(secondaryDataSource);
        return bean.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplateSecondary() throws Exception {
        return new SqlSessionTemplate(sqlSessionFactorySecondary());
    }
}
```
8. 执行单元测试
```
@RunWith(SpringRunner.class)
@SpringBootTest
public class SelfChapter39ApplicationTests {

    @Autowired(required = false)
    private UserMapperPrimary userMapperPrimary;
    @Autowired(required = false)
    private UserMapperSecondary userMapperSecondary;

    @Before
    public void setUp() {
        // 清空测试表，保证每次结果一样
        userMapperPrimary.deleteAll();
        userMapperSecondary.deleteAll();
    }

    @Test
    public void test() {
        // 往 Primary 数据源插入一条数据
        userMapperPrimary.insertUser("AAAA", 30);
        // 从Primary数据源查询刚才插入的数据，配置正确就可以查询到
        UserPrimary userPrimary = userMapperPrimary.findByName("AAAA");
        Assert.assertEquals(30, userPrimary.getAge().intValue());
        Assert.assertEquals("AAAA", userPrimary.getName());

        // 从Secondary数据源查询刚才插入的数据，配置正确应该是查询不到的
        UserSecondary userSecondary = userMapperSecondary.findByName("AAAA");
        Assert.assertNull(userSecondary);

        // 往Secondary数据源插入一条数据
        userMapperSecondary.insertUser("BBB", 20);

        // 从Primary数据源查询刚才插入的数据，配置正确应该是查询不到的
        userPrimary = userMapperPrimary.findByName("BBB");
        Assert.assertNull(userPrimary);

        // 从Secondary数据源查询刚才插入的数据，配置正确就可以查询到
        userSecondary = userMapperSecondary.findByName("BBB");
        Assert.assertEquals(20, userSecondary.getAge().intValue());
    }
}
```