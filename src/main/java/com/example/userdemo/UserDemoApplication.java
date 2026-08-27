package com.example.userdemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.userdemo.mapper")
public class UserDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserDemoApplication.class, args);
    }

}
