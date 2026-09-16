package com.example.ouradventure.service;

import com.example.ouradventure.common.exception.BusinessException;
import com.example.ouradventure.common.exception.ErrorCode;
import com.example.ouradventure.entity.CoupleInvitation;
import com.example.ouradventure.entity.enums.CoupleInvitationStatus;
import com.example.ouradventure.entity.enums.CoupleStatus;
import com.example.ouradventure.mapper.CoupleMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class CoupleService {

    // 排除易混字符 0/O/1/I：这个码要用户从聊天窗口手抄进输入框
    private static final String CODE_CHARS = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";

    private static final int CODE_LENGTH = 6;

    private static final int CODE_VALID_HOURS = 24;

    // 码空间 32^6 ≈ 10 亿，单次冲突概率极低；5 次只是给「连续手气差」一个体面的退出
    private static final int MAX_GENERATE_ATTEMPTS = 5;

    private final CoupleMapper coupleMapper;

    public CoupleService(CoupleMapper coupleMapper) {
        this.coupleMapper = coupleMapper;
    }

    public String generateInvitationCode(Long userId) {
        Long activeCoupleId = coupleMapper.findCoupleIdByUserIdAndStatus(userId, CoupleStatus.ACTIVE);

        if (activeCoupleId != null) {
            throw new BusinessException(ErrorCode.ALREADY_BOUND);
        }

        // 不做「先查一次码存不存在再插入」：查和插之间有并发窗口。
        // 直接插，让 uk_code 唯一约束做最终裁判，冲突了就换一个码重试
        for (int attempt = 1; attempt <= MAX_GENERATE_ATTEMPTS; attempt++) {
            CoupleInvitation invitation = new CoupleInvitation(
                    userId, randomCode(), LocalDateTime.now().plusHours(CODE_VALID_HOURS), CoupleInvitationStatus.PENDING);

            try {
                coupleMapper.addInvitation(invitation);
                return invitation.getCode();
            } catch (DuplicateKeyException e) {
                // 这里能断定冲突只可能来自 uk_code，因为 couple_invitations 上只有它一个唯一约束。
                // 将来若给 inviter_id 等列加唯一约束，这个前提就失效，必须改成显式区分冲突来源
            }
        }

        throw new BusinessException(ErrorCode.INVITATION_CODE_FAILED);
    }

    private String randomCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);

        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CODE_CHARS.charAt(random.nextInt(CODE_CHARS.length())));
        }

        return code.toString();
    }

}
