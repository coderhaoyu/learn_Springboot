package com.example.ouradventure.vo;

public class LoginVo {

    private  String token;

    private long expiresIn;

    private UserVo user;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expireIn) {
        this.expiresIn = expireIn;
    }

    public UserVo getUser() {
        return user;
    }

    public void setUser(UserVo user) {
        this.user = user;
    }


}
