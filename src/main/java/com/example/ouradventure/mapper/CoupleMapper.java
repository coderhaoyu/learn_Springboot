package com.example.ouradventure.mapper;

import com.example.ouradventure.entity.CoupleInvitation;
import com.example.ouradventure.entity.enums.CoupleStatus;
import org.apache.ibatis.annotations.Param;

public interface CoupleMapper {

    // 整张邀请一行写全，参数顺序不会在调用处读不懂；单个 POJO 参数也不需要 @Param
    void addInvitation(CoupleInvitation invitation);

    // 调用方只关心「有没有」，所以返回关系 id；返回 Couple 会让 status 等字段是隐性的 null
    Long findCoupleIdByUserIdAndStatus(@Param("userId") Long userId, @Param("status") CoupleStatus status);
}
