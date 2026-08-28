package com.example.userdemo.controller;

import com.example.userdemo.common.response.ApiResponse;
import com.example.userdemo.common.response.PageResult;
import com.example.userdemo.dto.CreateUserRequest;
import com.example.userdemo.dto.UpdateUserRequest;
import com.example.userdemo.dto.UserPageQueryRequest;
import com.example.userdemo.service.UserService;
import com.example.userdemo.entity.User;
import com.example.userdemo.vo.UserVo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public PageResult<UserVo> list(@Valid UserPageQueryRequest userPageQueryRequest) {
        return userService.findPage(userPageQueryRequest);
    }

    @GetMapping("/{id}")
    public ApiResponse<UserVo> getUserInfoById(@PathVariable long id) {
        UserVo user = userService.findById(id);
        return ApiResponse.ok(user);
    }

    @PostMapping()
    public ApiResponse<Boolean> addUser(@Valid @RequestBody CreateUserRequest userData) {
        userService.addUser(userData);
        return ApiResponse.ok(true);
    }

    @PutMapping("/{id}")
    public ApiResponse<Boolean> updateUser(@PathVariable long id, @Valid @RequestBody UpdateUserRequest userData) {
        userService.updateUser(id, userData);
        return ApiResponse.ok(true);
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
        return "Ok";
    }

    @GetMapping("/by-email")
    public ApiResponse<UserVo> getUserInfoByEmail(@RequestParam @NotBlank(message = "邮箱不能为空") @Email(message = "邮箱不正确") String email) {
        UserVo user = userService.getUserInfoByEmail(email);
        return ApiResponse.ok(user);
    }
}
