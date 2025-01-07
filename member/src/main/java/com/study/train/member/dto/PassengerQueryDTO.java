package com.study.train.member.dto;

import com.study.train.common.req.PageReq;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PassengerQueryDTO extends PageReq {

    private Long memberId;


    @Override
    public String toString() {
        return "PassengerQueryDTO{" +
                "memberId=" + memberId +
                '}';
    }
}