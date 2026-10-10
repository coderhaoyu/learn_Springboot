package com.example.ouradventure.coverter;

import com.example.ouradventure.entity.Couple;
import com.example.ouradventure.vo.CoupleVo;
import com.example.ouradventure.vo.UserVo;

public final class CoupleConverter {

    private CoupleConverter() {
    }

    public static CoupleVo toVo(Couple couple, UserVo partner) {
        CoupleVo vo = new CoupleVo();
        vo.setCoupleId(couple.getId());
        vo.setStatus(couple.getStatus());
        vo.setBoundAt(couple.getBoundAt());
        vo.setPartner(partner);

        return vo;
    }
}
