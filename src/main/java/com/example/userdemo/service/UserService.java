package com.example.userdemo.service;

import com.example.userdemo.common.exception.BusinessException;
import com.example.userdemo.common.response.PageResult;
import com.example.userdemo.dto.CreateUserRequest;
import com.example.userdemo.dto.UpdateUserRequest;
import com.example.userdemo.dto.UserPageQueryRequest;
import com.example.userdemo.entity.User;
import com.example.userdemo.mapper.UserMapper;
import com.example.userdemo.vo.UserVo;
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

    private UserVo convertToVo(User user) {
        if (user == null) {
            return null;
        }

        UserVo vo = new UserVo();
        vo.setId(user.getId());
        vo.setAge(user.getAge());
        vo.setEmail(user.getEmail());
        vo.setName(user.getName());

        return vo;
    }


//    private final List<User> userList = new ArrayList<>(List.of(new User(1L, "阿西", 38),
//            new User(2L, "阿光", 83)));

    public PageResult<UserVo> findPage(UserPageQueryRequest userPageQueryRequest) {
//        List<User> userList = userMapper.findAll();
//        List<UserVo> userVoList = new ArrayList<>();
//        for(User user : userList){
//            UserVo userVo = convertToVo(user);
//            userVoList.add(userVo);
//        }
//        return  userVoList;
        int page = userPageQueryRequest.getPage();
        int size = userPageQueryRequest.getSize();
        long total = userMapper.count();
        int offset = (page - 1) * size;

        List<User> userList = userMapper.findByPage(offset, size);

        List<UserVo> userVoList = userList.stream().map(this::convertToVo).toList();

        return new PageResult<UserVo>(userVoList, total, page, size);

    }

//    public User findById(long id) {
//        List<User> users = this.findAll();
//        for (User user : users) {
//            if (user.getId() == id) {
//                return user;
//            }
//        }
//        return null;
//    }

    public UserVo findById(long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return convertToVo(user);
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
        userMapper.deleteUser(id);
    }


    public UserVo getUserInfoByEmail(String email) {

        User user = userMapper.findUserByEmail(email);

        return convertToVo(user);
    }


}
