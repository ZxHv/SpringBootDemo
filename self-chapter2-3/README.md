## Java Bean Validation 请求参数校验
+ JSR是Java Specification Requests的缩写，意思是Java 规范提案。
### 1. 在校验的字段上添加 @NotNull 注解，如：
```
@Data
@ApiModel(description="用户实体")
public class User {
    @ApiModelProperty("用户编号")
    private Long id;

    @NotNull(message = "用户名称不能为空!")
    @Size(min = 2, max = 5, message = "用户名最少2个字,最多5个字!")
    @ApiModelProperty("用户姓名")
    private String name;

    @NotNull(message = "用户年龄不能为空!")
    @Max(value = 80, message = "年龄最大80岁")
    @Min(value = 18, message = "年龄最小18岁")
    @ApiModelProperty("用户年龄")
    private Integer age;

    @NotNull(message = "用户邮箱不能为空!")
    @Email
    @ApiModelProperty("用户邮箱")
    private String email;
}
```
### 2.在Controller中使用 @Validated 注解开启参数验证
```
@RequestMapping(value = "/users")
@Api(tags = "用户管理")
@RestController
@Validated
public class UserController {}
```
### 3.在具体的方法中使用 @Valid 注解开启参数验证
```
 @PostMapping("/")
    @ApiOperation(value = "创建用户", notes = "根据User对象创建用户")
    public String postUser(@Valid @RequestBody User user) {
        // @RequestBody注解用来绑定通过http请求中application/json类型上传的数据
        users.put(user.getId(), user);
        return "success";
    }
```
### 4.定义全局异常处理的返回信息类
```
@Data
public class RespVO {

    private String code;
    private String msg;
    private Long timestamp;

    public static RespVO ok(String msg){
        RespVO respVO = new RespVO();
        respVO.code = "200";
        respVO.setMsg(msg);
        respVO.timestamp = System.currentTimeMillis();
        return respVO;
    }

    public static RespVO error(String msg){
        RespVO respVO = new RespVO();
        respVO.code = "500";
        respVO.setMsg(msg);
        respVO.timestamp = System.currentTimeMillis();
        return respVO;
    }
}
```
### 5.处理全局异常，捕获 MethodArgumentNotValidException 异常并处理
```
@ControllerAdvice
public class UserExceptionHandler {
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public RespVO HandleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return RespVO.error(e.getBindingResult().getFieldError().getDefaultMessage());
    }
}
```
### 6.利用postman等工具进行测试