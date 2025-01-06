package com.study.train.member.resp;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MemberLoginResp {
    private Long id;

    private String mobile;

    private String token;

    @Override
    public String toString() {
        return "MemberLoginResp{" +
                "id=" + id +
                ", mobile='" + mobile + '\'' +
                ", JWTtoken='" + token + '\'' +
                '}';
    }

}