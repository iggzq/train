package com.study.train.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class MemberLoginDTO {

    @NotBlank(message = "【手机号】不能为空")
    @Pattern(regexp = "^1\\d{10}$",message = "手机号码格式错误")
    private String mobile;

    @NotBlank(message = "【短信验证码】不能为空")
    private String code;

    @Override
    public String toString() {
        return "MemberLoginDTO{" +
                "mobile='" + mobile + '\'' +
                ", code='" + code + '\'' +
                '}';
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
