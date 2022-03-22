package com.xxexample.selfchapter35.domain;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM USER WHERE NAME = #{name}")
    User findByName(@Param("name") String name);

    @Insert("INSERT INTO USER(NAME, AGE) VALUES(#{name}, #{age})")
    int insertUser(@Param("name") String name, @Param("age") Integer age);

    // 通过 Map<String, Object> 对象来作为传递参数的容器
    @Insert("INSERT INTO USER(NAME, AGE) VALUES(#{name,jdbcType=VARCHAR}, #{age,jdbcType=INTEGER})")
    int insertByMap(Map<String, Object> map);

    // 也可直接使用普通的 Java 对象来作为查询条件的传参
    @Insert("INSERT INTO USER(NAME, AGE) VALUES(#{name}, #{age})")
    int insertByUser(User user);

    @Delete("DELETE FROM USER WHERE id = #{id}")
    int deleteById(Long id);

    @Update("UPDATE USER SET AGE = #{age} WHERE NAME = #{name}")
    int updateUser(User user);

    // 对于查询，往往需要多表查询，查询结果可能是一个与数据库实体不同的包装类，可以通过以下方式进行返回结果绑定
    @Results({
            @Result(property = "name", column = "name"),
            @Result(property = "age", column = "age")
    })
    @Select("SELECT NAME, AGE FROM USER")
    List<User> findAll();

}
