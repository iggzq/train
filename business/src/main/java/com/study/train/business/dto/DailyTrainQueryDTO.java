package com.study.train.business.dto;

import com.study.train.common.req.PageReq;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class DailyTrainQueryDTO extends PageReq {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;
    private String code;

    @Override
    public String toString() {
        return "DailyTrainQueryDTO{" +
                "date=" + date +
                ", code='" + code + '\'' +
                "} " + super.toString();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}