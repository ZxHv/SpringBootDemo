## 接收 XML 格式请求并响应
### 实现原理
```
利用消息转换器(Message Converter)
在 Spring MVC 中定义了 HttpMessageConverter 接口，抽象了消息转换器对类型的判断、对读写的判断与操作
public interface HttpMessageConverter<T> {
    boolean canRead(Class<?> clazz, @Nullable MediaType mediaType);

    boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType);

    List<MediaType> getSupportedMediaTypes();

    default List<MediaType> getSupportedMediaTypes(Class<?> clazz) {
        return !this.canRead(clazz, (MediaType)null) && !this.canWrite(clazz, (MediaType)null) ? Collections.emptyList() : this.getSupportedMediaTypes();
    }

    T read(Class<? extends T> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException;

    void write(T t, @Nullable MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException;
}
```
### 实现过程
1. 引入 jackson-dataformat-xml 依赖
```
<dependency>
    <groupId>com.fasterxml.jackson.dataformat</groupId>
    <artifactId>jackson-dataformat-xml</artifactId>
</dependency>
```
2. 定义实体对象与 xml 的关系
```
@Data
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement(localName = "User")
public class User {
    @JacksonXmlProperty(localName = "name")
    private String name;

    @JacksonXmlProperty(localName = "age")
    private Integer age;
}
```
相关注解：  
@NoArgsConstructor、@AllArgsConstructor：生成无参、及全参构造函数  
@JacksonXmlRootElement、@JacksonXmlProperty：维护对象属性在xml中的对应关系  
上述配置的 User 对象，其对应的 xml 格式如下：
```
<User>
	<name>user111</name>
	<age>5</age>
</User>
```
3. 创建接收 xml 请求的 controller
```
@Controller
public class UserController {
    @PostMapping(value = "/user",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public User createUser(@RequestBody User user){
        user.setName("xxspace.com : " + user.getName());
        user.setAge(user.getAge() + 100);
        return user;
    }
}
```
4. 利用 postman 工具测试此接口
