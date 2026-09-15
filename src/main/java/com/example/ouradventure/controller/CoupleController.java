package com.example.ouradventure.controller;

import com.example.ouradventure.common.response.ApiResponse;
import com.example.ouradventure.service.CoupleService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/couples")
public class CoupleController {

    private final CoupleService coupleService;

    public CoupleController(CoupleService coupleService){
        this.coupleService = coupleService;
    }

    @GetMapping("/get-invitation-code")
    public ApiResponse<String> getInvitationCode(@AuthenticationPrincipal Long userId){
        String code = coupleService.generateInvitationCode(userId);
        return  ApiResponse.ok(code);
    }


}
