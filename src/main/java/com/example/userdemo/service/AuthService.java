package com.example.userdemo.service;

import com.example.userdemo.common.exception.BusinessException;
import com.example.userdemo.dto.RegisterRequest;
import com.example.userdemo.entity.User;
import com.example.userdemo.mapper.UserMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AuthService {

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    public AuthService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(RegisterRequest registerRequest) {

        User existUser = userMapper.findUserByEmail(registerRequest.getEmail());

        if (existUser != null) {
            throw new BusinessException(409, "邮箱已被注册");
        }

        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

        User user = new User();
        user.setName(registerRequest.getNickname());
        user.setEmail(registerRequest.getEmail().trim().toLowerCase(Locale.ROOT));
        user.setPassword(encodedPassword);

        try {
            userMapper.addUser(user);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(409, "邮箱已被注册");
        }
    }

}
