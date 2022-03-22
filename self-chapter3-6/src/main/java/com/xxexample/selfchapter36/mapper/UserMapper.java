package com.xxexample.selfchapter36.mapper;

import com.xxexample.selfchapter36.domain.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    User findByName(@Param("name") String name);

    int insert(@Param("name") String name, @Param("age") Integer age);
}
