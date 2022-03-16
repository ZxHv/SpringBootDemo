#Spring Boot配置详解
+ Spring Boot应用的配置内容都可以集中在src/main/resources/application.properties
+ SpringBoot的配置文件不仅可以使用properties文件，还可以使用YAML文件
+ YAML文件是以类似大纲的缩进形式来表示，如:
<br>

```
environment:
  dev:
      url: http:dev.bar.com
      name: Developer Setup
  prod:<br>
      url: http://foo.bar.com
      name: My Cool App
```

+ 等价的配置如下：
```
environment.dev.url=http:dev.bar.com
environment.dev.name=Developer Setup
environment.prod.url=http://foo.bar.com
environment.prod.name=My Cool App
```
+ YAML无法通过@PropertySource注解来加载配置，但它的属性加载到内存时，是有序的

+ 如何使用注解加载自定义配置参数
```
在application.properties中添加：
book.name=SpringBootLearning
book.author=xxxx
```
```
在应用中通过@Value注解加载自定义配置：
@Component
public class Book{
    @Value("${book.name}")
    private String name;
    @Value("${book.author}")
    private String author;
}
```
```
@Value 注解加载属性值的时候支持两种表达式：
1. 上面介绍的placeholder方式，格式为：${...}
2. SpEL表达式，格式为 #{...}
```

+ 配置文件内参数之间的引用，可以直接通过使用placeholder的方式：
```
book.name=SpringBootLearning
book.author=xxxx
book.desc=${book.author}  is writing《${book.name}》
```

+ 在配置文件内使用随机数参数  
秘钥、服务端口等我们希望它每次加载的时候不是一个固定的值，在SpringBoot中可以通过 ${random} 配置来产生随机数：
```
# 随机字符串
com.xxexample.blog.value=${random.value}
# 随机int
com.xxexample.blog.number=${random.int}
# 随机long
com.xxexample.blog.bignumber=${random.long}
# 10以内的随机数
com.xxexample.blog.randomNum10=${random.int(10)}
# 10-20的随机数
com.xxexample.blog.randomNum10To20=${random.int[10,20]}
```

+ 使用命令行指定application.properties中的参数值  
以命令行的方式启动SpringBoot应用时，连续两个减号 -- 就是对application.properties中的属性进行赋值的标识。  
所以，java -jar xxx.jar --server.port=8888，等价于在application.properties文件中添加属性server.port=8888
<br><br>
  
+ 多环境配置
```
在Spring Boot中多环境配置文件名须满足 application-{profile}.properties的格式
其中{profile}对应环境标识，如：
application-dev.properties：开发环境
application-test.properties：测试环境
application-prod.properties：生产环境

至于哪个具体的配置文件会被加载，需要在application.properties文件中通过spring.profiles.active属性来设置
```

+ Spring Boot对数据文件的加载顺序
```
1.命令行中传入的参数
2.SPRING_APPLICATION_JSON中的属性
3.java:comp/env中的JNDI属性
4.Java的系统属性，可以通过System.getProperties()获得的内容
5.操作系统的环境变量
6.通过random.*配置的随机属性
7.位于当前应用jar包之外，针对不同{profile}环境的配置文件内容，例如：application-{profile}.properties或是YAML定义的配置文件
8.位于当前应用jar包之内，针对不同{profile}环境的配置文件内容，例如：application-{profile}.properties或是YAML定义的配置文件
9.位于当前应用jar包之外的application.properties和YAML配置内容
10.位于当前应用jar包之内的application.properties和YAML配置内容
11.在@Configuration注解修改的类中，通过@PropertySource注解定义的属性
12.应用默认属性，使用SpringApplication.setDefaultProperties定义的内容
```
+ 在Spring Boot 2.0中增加了新的绑定API来帮助我们更容易的获取配置信息  
例子一：简单类型  
假设在properties文件中有一个配置: com.xxexample.foo=bar  
创建对应的配置类：
```
@Data
@ConfigurationProperties(prefix = "com.xxexample")
public class FooProperties {
    private String foo;
}
```
接下来通过最新的Binder类就可以获取配置信息：
````
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Application.class, args);
        Binder binder = Binder.get(context.getEnvironment());
        // 绑定简单配置
        FooProperties foo = binder.bind("com.xxexample", Bindable.of(FooProperties.class)).get();
        System.out.println(foo.getFoo());
    }
}
````
 例子二：List类型
 ```
com.xxexample.post[0]=Why Spring Boot
com.xxexample.post[1]=Why Spring Cloud

com.xxexample.posts[0].title=Why Spring Boot
com.xxexample.posts[0].content=It is perfect!
com.xxexample.posts[1].title=Why Spring Cloud
com.xxexample.posts[1].content=It is perfect too!
 ```
获取方式：
```
ApplicationContext context = SpringApplication.run(Application.class, args);
Binder binder = Binder.get(context.getEnvironment());
// 绑定List配置
List<String> post = binder.bind("com.xxexample.post", Bindable.listOf(String.class)).get();
List<PostInfo> posts = binder.bind("com.xxexample.posts", Bindable.listOf(PostInfo.class)).get();
```
