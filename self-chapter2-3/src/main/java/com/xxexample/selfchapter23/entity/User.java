package com.xxexample.selfchapter23.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;

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