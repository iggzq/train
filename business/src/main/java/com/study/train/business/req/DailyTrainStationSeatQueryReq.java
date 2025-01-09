package com.study.train.business.req;

import com.study.train.common.req.PageReq;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DailyTrainStationSeatQueryReq extends PageReq {

    private String trainCode;

    @Override
    public String toString() {
        return "DailyTrainStationSeatQueryReq{" +
                "trainCode='" + trainCode + '\'' +
                "} " + super.toString();
    }

}