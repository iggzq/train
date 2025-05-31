package com.study.train.member.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TicketQueryResp {

    /**
     * id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 会员id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long memberId;

    /**
     * 乘客id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long passengerId;

    /**
     * 乘客姓名
     */
    private String passengerName;

    /**
     * 日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date trainDate;

    /**
     * 车次编号
     */
    private String trainCode;

    /**
     * 箱序
     */
    private Integer carriageIndex;

    /**
     * 排号|01, 02
     */
    private String seatRow;

    /**
     * 列号|枚举[SeatColEnum]
     */
    private String seatCol;

    /**
     * 出发站
     */
    private String startStation;

    /**
     * 出发时间
     */
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    /**
     * 到达站
     */
    private String endStation;

    /**
     * 到站时间
     */
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    /**
     * 座位类型|枚举[SeatTypeEnum]
     */
    private String seatType;

    /**
     * 到达站
     */
    private Boolean publicShow;

    /**
     * 新增时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private String status;

    @Override
    public String toString() {
        return "TicketQueryResp{" +
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
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", status='" + status + '\'' +
                '}';
    }
}