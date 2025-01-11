package com.study.train.member.req;

import com.study.train.common.req.PageReq;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TicketQueryReq extends PageReq {

    private Long memberId;

    private String status;

    @Override
    public String toString() {
        return "TicketQueryReq{" +
                "memberId=" + memberId +
                ", status='" + status + '\'' +
                "} " + super.toString();
    }
}