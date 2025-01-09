package com.study.train.business.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class ConfirmOrderSaveReq {

    /**
     * id
     */
    private Long id;

    /**
     * 会员id
     */
    @NotNull(message = "【会员id】不能为空")
    private Long memberId;

    /**
     * 日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @NotNull(message = "【日期】不能为空")
    private Date date;

    /**
     * 车次编号
     */
    @NotBlank(message = "【车次编号】不能为空")
    private String trainCode;

    /**
     * 出发站
     */
    @NotBlank(message = "【出发站】不能为空")
    private String start;

    /**
     * 到达站
     */
    @NotBlank(message = "【到达站】不能为空")
    private String end;

    /**
     * 余票ID
     */
    @NotNull(message = "【余票ID】不能为空")
    private Long dailyTrainTicketId;

    /**
     * 车票
     */
    @NotBlank(message = "【车票】不能为空")
    private String tickets;

    /**
     * 订单状态|枚举[ConfirmOrderStatusEnum]
     */
    @NotBlank(message = "【订单状态】不能为空")
    private String status;

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

    @SuppressWarnings({"StringBufferReplaceableByString", "DuplicatedCode"})
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", memberId=").append(memberId);
        sb.append(", date=").append(date);
        sb.append(", trainCode=").append(trainCode);
        sb.append(", start=").append(start);
        sb.append(", end=").append(end);
        sb.append(", dailyTrainTicketId=").append(dailyTrainTicketId);
        sb.append(", tickets=").append(tickets);
        sb.append(", status=").append(status);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}