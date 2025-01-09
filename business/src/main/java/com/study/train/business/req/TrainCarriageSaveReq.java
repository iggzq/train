package com.study.train.business.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class TrainCarriageSaveReq {

    /**
     * id
     */
    private Long id;

    /**
     * 车次编号
     */
    @NotBlank(message = "【车次编号】不能为空")
    private String trainCode;

    /**
     * 厢号
     */
    @NotNull(message = "【厢号】不能为空")
    private Integer index;

    /**
     * 座位类型|枚举[SeatTypeEnum]
     */
    @NotBlank(message = "【座位类型】不能为空")
    private String seatType;

    /**
     * 座位数
     */
//    @NotNull(message = "【座位数】不能为空")
    private Integer seatCount;

    /**
     * 排数
     */
    @NotNull(message = "【排数】不能为空")
    private Integer rowCount;

    /**
     * 列数
     */
//    @NotNull(message = "【列数】不能为空")
    private Integer colCount;

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", trainCode=").append(trainCode);
        sb.append(", index=").append(index);
        sb.append(", seatType=").append(seatType);
        sb.append(", seatCount=").append(seatCount);
        sb.append(", rowCount=").append(rowCount);
        sb.append(", colCount=").append(colCount);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}