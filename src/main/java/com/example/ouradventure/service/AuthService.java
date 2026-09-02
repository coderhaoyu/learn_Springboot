package com.example.ouradventure.service;

import com.example.ouradventure.common.exception.BusinessException;
import com.example.ouradventure.common.security.JwtService;
import com.example.ouradventure.coverter.UserConverter;
import com.example.ouradventure.dto.LoginRequest;
import com.example.ouradventure.dto.RegisterRequest;
import com.example.ouradventure.entity.User;
import com.example.ouradventure.mapper.UserMapper;
import com.example.ouradventure.vo.LoginVo;
import com.example.ouradventure.vo.UserVo;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AuthService {

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    public AuthService(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public void registerUser(RegisterRequest registerRequest) {

        User existUser = userMapper.findUserByEmail(registerRequest.getEmail().trim().toLowerCase(Locale.ROOT));

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


    public LoginVo login(LoginRequest loginRequest) {

        String password = loginRequest.getPassword();
        String email = loginRequest.getEmail().trim().toLowerCase(Locale.ROOT);

        User user = userMapper.findAuthUserByEmail(email);
        if (user == null) {
            throw new BusinessException(401, "邮箱或密码错误");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(401, "邮箱或密码错误");
        }

        LoginVo loginVo = new LoginVo();
        loginVo.setUser(UserConverter.toVo(user));
        loginVo.setToken(jwtService.generateToken(user.getId()));
        loginVo.setExpiresIn(jwtService.getExpireSeconds());

        return loginVo;

    }

}
