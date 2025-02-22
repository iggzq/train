package com.study.train.business.enums;

import lombok.Getter;

@Getter
public enum RedisKeyPreEnum {
    CONFIRM_ORDER("LOCK_CONFIRM_ORDER:", "购票订单锁"),
    SK_TOKEN("SK_TOKEN:", "令牌锁"),
    SK_TOKEN_COUNT("SK_TOKEN_COUNT", "令牌数");

    private final String key;
    private final String desc;

    RedisKeyPreEnum(String key, String desc) {
        this.key = key;
        this.desc = desc;
    }

}
