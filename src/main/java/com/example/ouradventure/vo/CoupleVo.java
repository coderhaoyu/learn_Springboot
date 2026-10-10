package com.example.ouradventure.vo;

import com.example.ouradventure.entity.enums.CoupleStatus;

import java.time.LocalDateTime;

public class CoupleVo {

    private Long coupleId;

    private CoupleStatus status;

    private LocalDateTime boundAt;

    private UserVo partner;

    public Long getCoupleId() {
        return coupleId;
    }

    public void setCoupleId(Long coupleId) {
        this.coupleId = coupleId;
    }

    public CoupleStatus getStatus() {
        return status;
    }

    public void setStatus(CoupleStatus status) {
        this.status = status;
    }

    public LocalDateTime getBoundAt() {
        return boundAt;
    }

    public void setBoundAt(LocalDateTime boundAt) {
        this.boundAt = boundAt;
    }

    public UserVo getPartner() {
        return partner;
    }

    public void setPartner(UserVo partner) {
        this.partner = partner;
    }
}
