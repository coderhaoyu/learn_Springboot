package com.example.ouradventure.coverter;

import com.example.ouradventure.entity.User;
import com.example.ouradventure.vo.UserVo;

public final class UserConverter {
    private UserConverter() {
    }

    public static UserVo toVo(User user) {
        if (user == null) {
            return null;
        }

        UserVo vo = new UserVo();
        vo.setId(user.getId());
        vo.setAge(user.getAge());
        vo.setEmail(user.getEmail());
        vo.setName(user.getName());

        return vo;
    }
}
