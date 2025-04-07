package com.study.train.member.enums;

import lombok.Getter;

@Getter
public enum RedisKeyPreEnum {
    USER_LOGIN("USER_LOGIN:", "用户登录token前缀");

    private final String key;
    private final String desc;

    RedisKeyPreEnum(String key, String desc) {
        this.key = key;
        this.desc = desc;
    }

}
