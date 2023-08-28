package com.study.train.common.exception;

public enum BusinessExceptionEnum {
    MEMBER_MOBILE_EXIST("手机号已注册"),
    MEMBER_MOBILE_NOT_EXIST("手机号未注册"),

    MEMBER_MOBILE_CODE_ERROR("手机验证码错误");
    private String desc;


    BusinessExceptionEnum(String desc) {
        this.desc = desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return "BusinessExceptionEnum{" +
                "desc='" + desc + '\'' +
                '}';
    }

}
