package com.mayi.book;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.mayi.book.mapper")
public class MayiBookApplication {
    public static void main(String[] args) {
        SpringApplication.run(MayiBookApplication.class, args);
    }
}
