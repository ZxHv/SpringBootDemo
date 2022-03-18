## 构建RESTful API与单元测试
### 注解解释
+ @RestController：Spring4之后加入的注解，原来在@Controller中返回json需要@ResponseBody来配合
+ @RequestMapping：配置url映射。现在更多的也会直接用以Http Method直接关联的映射注解来定义，比如：GetMapping、PostMapping、DeleteMapping、PutMapping等
+ @PathVariable：参数绑定
+ @Data：要使用 @Data 注解要先引入lombok,使用这个注解可以省去代码中大量的get(), set(),toString()等方法
+ @Autowired：可以标注在属性上、方法上和构造器上，来完成自动装配

### get请求返回中文乱码问题
```
在get请求中加入produces= "application/json;charset=UTF-8"
```

### 单元测试
须引用下面这些静态函数的引用
```
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
```
