package com.example.userdemo.service;

import com.example.userdemo.common.response.PageResult;
import com.example.userdemo.dto.CreateUserRequest;
import com.example.userdemo.dto.UpdateUserRequest;
import com.example.userdemo.dto.UserPageQueryRequest;
import com.example.userdemo.entity.User;
import com.example.userdemo.mapper.UserMapper;
import com.example.userdemo.vo.UserVo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


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

        List<UserVo> userVoList =  userList.stream().map(this::convertToVo).toList();

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
        return convertToVo(userMapper.findById(id));
    }

    public void addUser(CreateUserRequest createUserRequest) {
        User user = new User();
        user.setName(createUserRequest.getName());
        user.setAge(createUserRequest.getAge());
        user.setEmail(createUserRequest.getEmail());
        userMapper.addUser(user);
    }

    public void updateUser(long id, UpdateUserRequest updateUserRequest) {
        User user = new User();
        user.setName(updateUserRequest.getName());
        user.setAge(updateUserRequest.getAge());
        user.setEmail(updateUserRequest.getEmail());
        user.setId(id);
        userMapper.updateUser(user);
    }

    public void deleteUser(long id) {
        userMapper.deleteUser(id);
    }


    public UserVo getUserInfoByEmail(String email){

        User user = userMapper.findUserByEmail(email);

        return convertToVo(user);
    }


}
