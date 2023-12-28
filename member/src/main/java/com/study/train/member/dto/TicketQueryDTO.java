package com.study.train.member.dto;

import com.study.train.common.req.PageReq;

public class TicketQueryDTO extends PageReq {

    private Long memberId;

    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

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
                ", status='" + status + '\'' +
                "} " + super.toString();
    }
}