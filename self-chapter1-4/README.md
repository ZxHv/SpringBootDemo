#配置元数据
###创建一个配置类
```
@Data
@Configuration
@ConfigurationProperties(prefix = "com.xxspace")
public class XXProperties {
    /**
     * 这是一个自定义配置
     */
    private String from;
}
```
### 在 pom.xml 中添加自动生成配置元数据的依赖
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
</dependency>
```

### mvn install一下这个项目
```
在terminal中cd到module self-chapter-1-4,随后执行：
mvn install
此时在module的target/classess/META-INF目录下找到元数据json文件
此时，在配置文件中编写此自定义配置项时，编辑器会自动给出联想和提示
```
