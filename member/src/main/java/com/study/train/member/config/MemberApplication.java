package com.study.train.member.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.study")
public class MemberApplication {

    public static void main(String[] args) {
        System.out.println("hello world12345");
        SpringApplication.run(MemberApplication.class, args);
    }

}
