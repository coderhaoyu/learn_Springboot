package com.example.ouradventure.service;

import com.example.ouradventure.common.exception.BusinessException;
import com.example.ouradventure.common.response.PageResult;
import com.example.ouradventure.coverter.UserConverter;
import com.example.ouradventure.dto.CreateUserRequest;
import com.example.ouradventure.dto.UpdateUserRequest;
import com.example.ouradventure.dto.UserPageQueryRequest;
import com.example.ouradventure.entity.User;
import com.example.ouradventure.mapper.UserMapper;
import com.example.ouradventure.vo.UserVo;
import org.springframework.stereotype.Service;

import java.nio.BufferUnderflowException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
public class UserService {

    private final UserMapper userMapper;


    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }


    public PageResult<UserVo> findPage(UserPageQueryRequest userPageQueryRequest) {

        int page = userPageQueryRequest.getPage();
        int size = userPageQueryRequest.getSize();
        long total = userMapper.count();
        int offset = (page - 1) * size;

        List<User> userList = userMapper.findByPage(offset, size);

        List<UserVo> userVoList = userList.stream().map(UserConverter::toVo).toList();

        return new PageResult<UserVo>(userVoList, total, page, size);

    }

    public UserVo findById(long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return UserConverter.toVo(user);
    }

    public void addUser(CreateUserRequest createUserRequest) {
        UserVo user = getUserInfoByEmail(createUserRequest.getEmail());
        if (user != null) {
            throw new BusinessException(409, "邮箱已存在");
        }
        User newUser = new User();
        newUser.setName(createUserRequest.getName());
        newUser.setAge(createUserRequest.getAge());
        newUser.setEmail(createUserRequest.getEmail());
        userMapper.addUser(newUser);


    }

    public void updateUser(long id, UpdateUserRequest updateUserRequest) {
        findById(id);
        UserVo existUserByEmail = getUserInfoByEmail(updateUserRequest.getEmail());

//      查重：只有在查出记录 且 查出的记录不是自己 时才拦截
        if (existUserByEmail != null && !Objects.equals(existUserByEmail.getId(), id)) {
            throw new BusinessException(409, "邮箱已经存在");
        }

        User newUser = new User();
        newUser.setName(updateUserRequest.getName());
        newUser.setAge(updateUserRequest.getAge());
        newUser.setEmail(updateUserRequest.getEmail());
        newUser.setId(id);
        userMapper.updateUser(newUser);
    }

    public void deleteUser(long id) {
        findById(id);
        userMapper.deleteUser(id);
    }


    public UserVo getUserInfoByEmail(String email) {

        User user = userMapper.findUserByEmail(email);

        return UserConverter.toVo(user);
    }


}
