package com.example.ouradventure.dto;

import jakarta.validation.constraints.NotBlank;

public record BindInvitationRequest(
        @NotBlank(message = "邀请码不能为空") String code) {
}
