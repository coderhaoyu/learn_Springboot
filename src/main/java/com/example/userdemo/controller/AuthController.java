package com.example.userdemo.controller;

import com.example.userdemo.common.response.ApiResponse;
import com.example.userdemo.dto.RegisterRequest;
import com.example.userdemo.service.AuthService;
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

    public AuthController(AuthService authService){
        this.authService = authService;
    }


    @PostMapping("/register")
    public ApiResponse<Boolean> registerUser(@Valid @RequestBody RegisterRequest registerRequest){
        authService.registerUser(registerRequest);
        return  ApiResponse.ok(true);
    }

}
