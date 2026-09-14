package com.example.ouradventure.entity;

import com.example.ouradventure.entity.enums.CoupleStatus;

import java.time.LocalDateTime;

public class Couple {

    private Long id;

    private Long userAId;

    private Long userBId;

    private CoupleStatus status;

    private LocalDateTime boundAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    public Couple(){

    }

    public Couple (Long userAId, Long userBId, CoupleStatus status, LocalDateTime boundAt){
        this.userAId = userAId;
        this.userBId = userBId;
        this.status = status;
        this.boundAt = boundAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserAId() {
        return userAId;
    }

    public void setUserAId(Long userAId) {
        this.userAId = userAId;
    }

    public Long getUserBId() {
        return userBId;
    }

    public void setUserBId(Long userBId) {
        this.userBId = userBId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
