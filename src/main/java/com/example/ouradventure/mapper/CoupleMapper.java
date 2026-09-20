package com.example.ouradventure.mapper;

import com.example.ouradventure.entity.CoupleInvitation;
import com.example.ouradventure.entity.enums.CoupleStatus;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

public interface CoupleMapper {

    // 整张邀请一行写全，参数顺序不会在调用处读不懂；单个 POJO 参数也不需要 @Param
    void addInvitation(CoupleInvitation invitation);

    // 调用方只关心「有没有」，所以返回关系 id；返回 Couple 会让 status 等字段是隐性的 null
    Long findCoupleIdByUserIdAndStatus(@Param("userId") Long userId, @Param("status") CoupleStatus status);

    void addCouple(@Param("userAId") Long userAId, @Param("userBId") Long userBId, @Param("status") CoupleStatus status, @Param("boundAt") LocalDateTime boundAt);

    CoupleInvitation findInvitation(String code);

    // 返回受影响行数：调用方靠它判断「这个码是不是被我抢到的」。
    // 返回 void 就只能靠事先查一次状态，而查完到更新之间存在并发窗口
    int markInvitationUsed(@Param("code") String code, @Param("usedAt") LocalDateTime usedAt);

}
