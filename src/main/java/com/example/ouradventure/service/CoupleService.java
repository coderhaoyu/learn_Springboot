package com.example.ouradventure.service;

import com.example.ouradventure.common.exception.BusinessException;
import com.example.ouradventure.entity.Couple;
import com.example.ouradventure.entity.enums.CoupleInvitationStatus;
import com.example.ouradventure.entity.enums.CoupleStatus;
import com.example.ouradventure.mapper.CoupleMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class CoupleService {

    private final CoupleMapper coupleMapper;

    public CoupleService(CoupleMapper coupleMapper) {
        this.coupleMapper = coupleMapper;
    }

    public String generateInvitationCode(Long userId) {
        Couple activeCouple = coupleMapper.findActiveByUserId(userId);

        if (activeCouple != null) {
            throw new BusinessException(409, "您已经有了绑定了，不能再发邀请码了");
        }

        for (int attempt = 1; attempt <= 5; attempt++) {
            String code = generateCodeRandomCode();
            LocalDateTime expiresAt = LocalDateTime.now().plusHours(24);
            try {
                coupleMapper.addInvitationCode(userId, code, expiresAt, CoupleInvitationStatus.PENDING);
                return code;
            } catch (DuplicateKeyException e) {

            }
        }

        throw new BusinessException(500, "邀请码生成失败，请重试");
    }

    private String generateCodeRandomCode() {
        final String chars = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder(6);

        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }

        return sb.toString();
    }

}
