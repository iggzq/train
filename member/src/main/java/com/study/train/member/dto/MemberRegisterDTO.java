package com.study.train.member.dto;

public class MemberRegisterDTO {

    private String mobile;

    @Override
    public String toString() {
        return "MemberRegisterDTO{" +
                "mobile='" + mobile + '\'' +
                '}';
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
