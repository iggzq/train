package com.study.train.member.dto;

import jakarta.validation.constraints.NotBlank;

public class MemberRegisterDTO {

    @NotBlank(message = "手机号不能为空")
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
