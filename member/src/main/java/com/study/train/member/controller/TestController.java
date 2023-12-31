package com.study.train.member.controller;

import jakarta.annotation.Resource;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Resource
    Environment environment;

    @GetMapping("/hello")
    public String hello() {
        String property = environment.getProperty("local.server.port");
        return ("hello world  " + property);
    }
}
