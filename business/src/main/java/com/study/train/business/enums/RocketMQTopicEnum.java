package com.study.train.business.enums;

import lombok.Getter;

@Getter
public enum RocketMQTopicEnum {

    CONFIRM_ORDER("CONFIRM_ORDER", "确认订单排队");

    private final String key;
    private final String desc;

    RocketMQTopicEnum(String key, String desc) {
        this.key = key;
        this.desc = desc;
    }

}
