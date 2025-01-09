package com.study.train.business.req;

import com.study.train.common.req.PageReq;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Setter
@Getter
public class DailyTrainQueryReq extends PageReq {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;
    private String code;

    @Override
    public String toString() {
        return "DailyTrainQueryReq{" +
                "date=" + date +
                ", code='" + code + '\'' +
                "} " + super.toString();
    }

}