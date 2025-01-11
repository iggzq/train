package com.study.train.member.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class Ticket {
    private Long id;

    private Long memberId;

    private Long passengerId;

    private String passengerName;

    private Date trainDate;

    private String trainCode;

    private Integer carriageIndex;

    private String seatRow;

    private String seatCol;

    private String startStation;

    private Date startTime;

    private String endStation;

    private Date endTime;

    private String seatType;

    private String status;

    private Date createTime;

    private Date updateTime;

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", memberId=" + memberId +
                ", passengerId=" + passengerId +
                ", passengerName='" + passengerName + '\'' +
                ", trainDate=" + trainDate +
                ", trainCode='" + trainCode + '\'' +
                ", carriageIndex=" + carriageIndex +
                ", seatRow='" + seatRow + '\'' +
                ", seatCol='" + seatCol + '\'' +
                ", startStation='" + startStation + '\'' +
                ", startTime=" + startTime +
                ", endStation='" + endStation + '\'' +
                ", endTime=" + endTime +
                ", seatType='" + seatType + '\'' +
                ", status='" + status + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}