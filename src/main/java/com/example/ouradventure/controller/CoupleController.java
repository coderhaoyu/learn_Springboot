package com.example.ouradventure.controller;

import com.example.ouradventure.common.response.ApiResponse;
import com.example.ouradventure.service.CoupleService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/couples")
public class CoupleController {

    private final CoupleService coupleService;

    public CoupleController(CoupleService coupleService) {
        this.coupleService = coupleService;
    }

    // 签发邀请码会写入一行新记录，属于「创建资源」，必须用 POST：
    // GET 在 HTTP 语义里要求安全且幂等，浏览器预取、代理重试、用户刷新都会凭空多签一个码
    @PostMapping("/invitations")
    public ApiResponse<String> createInvitation(@AuthenticationPrincipal Long userId) {
        String code = coupleService.generateInvitationCode(userId);
        return ApiResponse.ok(code);
    }

}
