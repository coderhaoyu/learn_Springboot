package com.example.ouradventure.controller;

import com.example.ouradventure.common.response.ApiResponse;
import com.example.ouradventure.common.response.PageResult;
import com.example.ouradventure.dto.CreateUserRequest;
import com.example.ouradventure.dto.UpdateUserRequest;
import com.example.ouradventure.dto.UserPageQueryRequest;
import com.example.ouradventure.service.UserService;
import com.example.ouradventure.entity.User;
import com.example.ouradventure.vo.UserVo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @PutMapping("/{id}")
    public ApiResponse<Boolean> updateUser(@PathVariable long id, @Valid @RequestBody UpdateUserRequest userData) {
        userService.updateUser(id, userData);
        return ApiResponse.ok(true);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
        return ApiResponse.ok(true);
    }

    @GetMapping("/by-email")
    public ApiResponse<UserVo> getUserInfoByEmail(@RequestParam @NotBlank(message = "邮箱不能为空") @Email(message = "邮箱不正确") String email) {
        UserVo user = userService.getUserInfoByEmail(email);
        return ApiResponse.ok(user);
    }

//    @GetMapping("/me")
//    public ApiResponse<UserVo> getCurrentUserInfo() {
//        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        UserVo user = userService.findById(userId);
//        return ApiResponse.ok(user);
//    }

    @GetMapping("/me")
    public ApiResponse<UserVo> getCurrentUserInfo(@AuthenticationPrincipal Long userId) {
        UserVo user = userService.findById(userId);
        return ApiResponse.ok(user);
    }
}
