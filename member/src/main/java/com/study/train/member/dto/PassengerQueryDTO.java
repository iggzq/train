package com.study.train.member.dto;

public class PassengerQueryDTO {

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