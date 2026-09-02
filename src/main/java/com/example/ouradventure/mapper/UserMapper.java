package com.example.ouradventure.mapper;

import com.example.ouradventure.dto.UpdateUserRequest;
import com.example.ouradventure.entity.User;
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

    User findAuthUserByEmail(String email);
}

