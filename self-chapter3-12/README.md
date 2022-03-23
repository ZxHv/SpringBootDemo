## 使用 JTA 实现多数据源的事务管理
+ JTA，全称：Java Transaction API。JTA 事务比 JDBC 事务更强大。一个 JTA 事务可以有多个参与者，而一个 JDBC 事务则被限定在一个单一的数据库连接。
+ 例：
  比如：在订单库中创建一条订单记录，同时还需要在商品库中扣减商品库存。如果库存扣减失败，那么我们希望订单创建也能够回滚。  
  如果扣减商品库存的操作和订单创建的操作位于不同的数据库中，JDBC 则无法同时回滚两个操作。此时，使用 JTA 事务就可以弥补 JDBC 事务的不足
### 使用 JTA 的实现 Atomikos 来实现多数据源下的事务管理
1. 场景设定
```
a) 假定有两个不同的数据库，springboot_test、springboot_test2
b) 这两个数据库中都有一张 User 表，两张表中都有一条数据，name=aaa,age=30
```
2. 引入相关依赖
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jta-atomikos</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
```
3. 在 application.properties 配置文件中配置两个 springboot_test、springboot_test2 数据源
```
spring.jta.enabled=true

spring.jta.atomikos.datasource.primary.xa-properties.url=jdbc:mysql://localhost:3306/springboot_test
spring.jta.atomikos.datasource.primary.xa-properties.user=root
spring.jta.atomikos.datasource.primary.xa-properties.password=root
spring.jta.atomikos.datasource.primary.xa-data-source-class-name=com.mysql.cj.jdbc.MysqlXADataSource
spring.jta.atomikos.datasource.primary.unique-resource-name=test1
spring.jta.atomikos.datasource.primary.max-pool-size=25
spring.jta.atomikos.datasource.primary.min-pool-size=3
spring.jta.atomikos.datasource.primary.max-lifetime=20000
spring.jta.atomikos.datasource.primary.borrow-connection-timeout=10000

spring.jta.atomikos.datasource.secondary.xa-properties.url=jdbc:mysql://localhost:3306/springboot_test2
spring.jta.atomikos.datasource.secondary.xa-properties.user=root
spring.jta.atomikos.datasource.secondary.xa-properties.password=root
spring.jta.atomikos.datasource.secondary.xa-data-source-class-name=com.mysql.cj.jdbc.MysqlXADataSource
spring.jta.atomikos.datasource.secondary.unique-resource-name=test2
spring.jta.atomikos.datasource.secondary.max-pool-size=25
spring.jta.atomikos.datasource.secondary.min-pool-size=3
spring.jta.atomikos.datasource.secondary.max-lifetime=20000
spring.jta.atomikos.datasource.secondary.borrow-connection-timeout=10000
```
4. 创建多数据源配置类 DataSourceConfiguration
```
@Configuration
public class DataSourceConfiguration {

    /**
     * 数据源改为 AtomikosDataSourceBean 创建而不是 DataSourceBuilder，否则 jdbcTemplate 操作数据库无法正常回滚。
     * @return
     */
    @Primary
    @Bean(name = "primaryDataSource")
    @ConfigurationProperties(prefix = "spring.jta.atomikos.datasource.primary")
    public DataSource primaryDataSource() {
        return new AtomikosDataSourceBean();
    }

    @Bean(name = "secondaryDataSource")
    @ConfigurationProperties(prefix = "spring.jta.atomikos.datasource.secondary")
    public DataSource secondaryDataSource() {
        return new AtomikosDataSourceBean();
    }

    @Bean(name = "primaryJdbcTemplate")
    public JdbcTemplate primaryJdbcTemplate(@Qualifier("primaryDataSource") DataSource primaryDataSource) {
        return new JdbcTemplate(primaryDataSource);
    }

    @Bean(name = "secondaryJdbcTemplate")
    public JdbcTemplate secondaryJdbcTemplate(@Qualifier("secondaryDataSource") DataSource secondaryDataSource) {
        return new JdbcTemplate(secondaryDataSource);
    }
}
```
5. 创建一个 service 的测试实现类，省略接口类
```
@Service
public class TestService {

    private JdbcTemplate primaryJdbcTemplate;
    private JdbcTemplate secondaryJdbcTemplate;

    public TestService(JdbcTemplate primaryJdbcTemplate, JdbcTemplate secondaryJdbcTemplate) {
        this.primaryJdbcTemplate = primaryJdbcTemplate;
        this.secondaryJdbcTemplate = secondaryJdbcTemplate;
    }

    /**
     * 此方法正常情况下都会成功
     */
    @Transactional
    public void tx1() {
        // 修改test1库中的数据
        primaryJdbcTemplate.update("update user set age = ? where name = ?", 50, "aaa");
        // 修改test2库中的数据
        secondaryJdbcTemplate.update("update user set age = ? where name = ?", 50, "aaa");
    }

    /**
     * 人为的制造了一个异常，此异常在 primary 数据库的数据执行更新操作以后才产生，可以测试能否在遇到异常时能在 JTA 的帮助下实现回滚
     */
    @Transactional
    public void tx2() {
        // 修改test1库中的数据
        primaryJdbcTemplate.update("update user set age = ? where name = ?", 40, "aaa");
        // 模拟：修改test2库之前抛出异常
        throw new RuntimeException();
    }
}
```
6. 编写单元测试
```
import com.xxexample.selfchapter312.service.TestService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SelfChapter312ApplicationTests {

    @Autowired
    private JdbcTemplate primaryJdbcTemplate;
    @Autowired
    private JdbcTemplate secondaryJdbcTemplate;

    @Autowired
    private TestService testService;

    @Test
    public void test1() {
        // 正确更新的情况
        testService.tx1();
        String sql1 = "select age from user where name=?";
        Assert.assertEquals(50, primaryJdbcTemplate.queryForObject(sql1, Integer.class, "aaa").intValue());
        Assert.assertEquals(50, secondaryJdbcTemplate.queryForObject(sql1, Integer.class, "aaa").intValue());
    }

    @Test
    public void test2() {
        // 更新失败的情况
        try {
            testService.tx2();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 部分更新失败，test1中的更新应该回滚
            String sql2 = "select age from user where name=?";
            Assert.assertEquals(50, primaryJdbcTemplate.queryForObject(sql2, Integer.class, "aaa").intValue());
            Assert.assertEquals(50, secondaryJdbcTemplate.queryForObject(sql2, Integer.class, "aaa").intValue());
        }
    }
}
```
7. 单元测试结果
```
1. test1() 方法能执行成功，两个数据库的 User 表中 name=aaa 的用户的 age 都会更新为 50
2. test2() 方法首先将 primary 数据库中 name=aaa 的用户的 age 的值更新为 40，接着抛出异常后，JTA 事务生效，会将 age 回滚。
3. 同时，在 transaction-logs 目录下，还能找到关于事务的日志信息
```