package com.study.train.member.dto;

import com.study.train.common.req.PageReq;

public class TicketQueryDTO extends PageReq {

    private Long memberId;

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    @Override
    public String toString() {
        return "TicketQueryDTO{" +
                "memberId=" + memberId +
                "} " + super.toString();
    }
}