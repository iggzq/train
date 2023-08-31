package com.study.train.member.dto;

import com.study.train.common.req.PageReq;

public class PassengerQueryDTO extends PageReq {

    private Long memberId;


    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }


    @Override
    public String toString() {
        return "PassengerQueryDTO{" +
                "memberId=" + memberId +
                '}';
    }
}