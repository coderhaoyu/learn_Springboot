package com.example.ouradventure;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.ouradventure.mapper")
public class OurAdventureApplication {

    public static void main(String[] args) {
        SpringApplication.run(OurAdventureApplication.class, args);
    }

}
