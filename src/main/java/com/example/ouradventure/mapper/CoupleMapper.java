package com.example.ouradventure.mapper;

import com.example.ouradventure.entity.Couple;
import com.example.ouradventure.entity.enums.CoupleInvitationStatus;


import java.time.LocalDateTime;

public interface CoupleMapper {
    void addInvitationCode(Long inviterId, String code, LocalDateTime expiresAt , CoupleInvitationStatus status);

    Couple findActiveByUserId(Long userId);

}
