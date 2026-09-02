package com.example.ouradventure.controller;

import com.example.ouradventure.common.response.ApiResponse;
import com.example.ouradventure.dto.LoginRequest;
import com.example.ouradventure.dto.RegisterRequest;
import com.example.ouradventure.service.AuthService;
import com.example.ouradventure.vo.UserVo;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/register")
    public ApiResponse<Boolean> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        authService.registerUser(registerRequest);
        return ApiResponse.ok(true);
    }

    @PostMapping("/login")
    public ApiResponse<UserVo> login(@Valid @RequestBody LoginRequest loginRequest) {

        UserVo userVo = authService.login(loginRequest);
        return ApiResponse.ok(userVo);
    }

}
