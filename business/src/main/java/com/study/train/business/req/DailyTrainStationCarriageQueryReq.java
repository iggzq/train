package com.study.train.business.req;

import com.study.train.common.req.PageReq;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Setter
@Getter
public class DailyTrainStationCarriageQueryReq extends PageReq {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    private String trainCode;

    @Override
    public String toString() {
        return "DailyTrainStationCarriageQueryReq{" +
                "date=" + date +
                ", trainCode='" + trainCode + '\'' +
                "} " + super.toString();
    }

}