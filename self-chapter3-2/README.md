## 默认数据源Hikari的配置详解
### 为什么需要数据源？
```
1.封装关于数据库访问的各种参数，实现统一管理
2.通过对数据库的连接池管理，节省开销并提高效率
```
### 优秀的开源数据源
```
DBCP
C3P0
Druid
HikariCP：Sprig Boot 默认数据源，目前性能最佳
```

### Spring Boot 的自动化配置中，对数据源的配置可以分为两类：
1.通用配置，以 Spring.datasource.* 的形式存在，主要是数据库的通用配置，如：数据库连接地址、用户名、密码等
```
spring.datasource.url=jdbc:mysql://localhost:3306/springboot_test
spring.datasource.username=root
spring.datasource.password=root
#spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```
2.数据源连接池配置：以 Spring.datasource.<数据源名称>.* 的形式存在：
```
# 最小空闲连接，默认为10。小于0或者大于 maximum-pool-size 都会被重置为 maximum-pool-size
spring.datasource.hikari.minimum-idle=10
# 最大连接数，小于等于0会被重置为默认值10；大于0小于1会被重置为 minimum-idle
spring.datasource.hikari.maximum-pool-size=20
# 空闲连接超时时间，默认值为 600000（10分钟），大于等于 max-lifetime 且 max-lifetime > 0，会被重置为0；
# 不等于0且小于10秒，则会被重置为10秒
spring.datasource.hikari.idle-timeout=500000
# 连接最大存活时间，不等于0且小于30秒，会被重置为默认值30分钟，设置应该比 mysql 的超时时间短
spring.datasource.hikari.max-lifetime=540000
# 连接超时时间：毫秒，小于250毫秒，否则会被重置为默认值30秒
spring.datasource.hikari.connection-timeout=60000
# 用于测试连接是否可用的查询语句
spring.datasource.hikari.connection-test-query=SELECT 1
```