package com.study.train.business.req;

import com.study.train.common.req.PageReq;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Setter
@Getter
public class DailyTrainTicketQueryReq extends PageReq {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    private String trainCode;

    private String start;

    private String end;

    @Override
    public String toString() {
        return "DailyTrainTicketQueryReq{" +
                "date=" + date +
                ", trainCode='" + trainCode + '\'' +
                ", start='" + start + '\'' +
                ", end='" + end + '\'' +
                "} " + super.toString();
    }

}