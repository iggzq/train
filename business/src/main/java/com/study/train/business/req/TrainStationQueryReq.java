package com.study.train.business.req;

import com.study.train.common.req.PageReq;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TrainStationQueryReq extends PageReq {

    private String trainCode;

    @Override
    public String toString() {
        return "TrainStationQueryReq{" +
                "trainCode='" + trainCode + '\'' +
                "} " + super.toString();
    }

}