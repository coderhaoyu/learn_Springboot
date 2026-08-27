package com.example.userdemo.mapper;

import com.example.userdemo.dto.UpdateUserRequest;
import com.example.userdemo.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {

    User findById(long id);

    List<User> findByPage(@Param("offset") int offset, @Param("limit") int limit);

    long count();

    void addUser(User user);

    void updateUser(User user);

    void deleteUser(long id);

    User findUserByEmail(String email);
}

