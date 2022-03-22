## JdbcTemplate 的多数据源配置
+ 当需要多个数据源的时候，该如何配置
### 多数据源配置过程
1. 引入相关依赖
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.13.2</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.20</version>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
```
2. 多数据源配置
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
3. 初始化数据源与 JdbcTemplate
```
@Configuration
public class DataSourceConfiguration {

    // 不特别指定哪个数据源的时候，就会使用这个 Bean
    @Primary
    @Bean
    //加载 spring.datasource.primary.*
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    public DataSource primaryDataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean
    //加载 spring.datasource.secondary.*
    @ConfigurationProperties(prefix = "spring.datasource.secondary")
    public DataSource secondaryDataSource(){
        return DataSourceBuilder.create().build();
    }

    /**
     * 创建 JdbcTemplate 时，注入了 primaryDataSource 数据源
     * @param primaryDataSource
     * @return
     */
    @Bean
    public JdbcTemplate primaryJdbcTemplate(@Qualifier("primaryDataSource") DataSource primaryDataSource){
        return new JdbcTemplate(primaryDataSource);
    }

    /**
     * 创建 JdbcTemplate 时，注入了 secondaryDataSource 数据源
     * @param secondaryDataSource
     * @return
     */
    @Bean
    public JdbcTemplate secondaryJdbcTemplate(@Qualifier("secondaryDataSource") DataSource secondaryDataSource){
        return new JdbcTemplate(secondaryDataSource);
    }
}
```
4. 单元测试
```
@RunWith(SpringRunner.class)
@SpringBootTest
public class SelfChapter37ApplicationTests {

    @Autowired
    private JdbcTemplate primaryJdbcTemplate;

    @Autowired
    private JdbcTemplate secondaryJdbcTemplate;

    @Before
    public void setUp(){
        primaryJdbcTemplate.update("DELETE FROM USER");
        secondaryJdbcTemplate.update("DELETE FROM USER");
    }

    @Test
    public void test() {
        // 往第一个数据源中插入2条数据
        primaryJdbcTemplate.update("INSERT INTO USER(NAME, AGE) VALUES(?, ?)", "aaa", 20);
        primaryJdbcTemplate.update("INSERT INTO USER(NAME, AGE) VALUES(?, ?)", "bbb", 30);

        // 往第二个数据源中插入 1 条数据，若插入的是第一个数据源，则会主键冲突报错
        secondaryJdbcTemplate.update("INSERT INTO USER(NAME, AGE) VALUES(?, ?)", "ccc", 20);

        // 查一下第一个数据源中是否有 2 条数据，验证插入是否成功
        Assert.assertEquals("2", primaryJdbcTemplate.queryForObject("select count(1) from user", String.class));

        Assert.assertEquals("1", secondaryJdbcTemplate.queryForObject("select count(1) from user", String.class));
    }
}
```
注意：  
1）当未使用 @Qualifier 指定 JdbcTemplate 时，会采用参数的名字来查找 Bean，存在的话就注入  
2）这两个 JdbcTemplate 在创建时，会默认使用方法名称来作为 bean 的名称