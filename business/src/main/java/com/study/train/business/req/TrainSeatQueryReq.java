package com.study.train.business.req;

import com.study.train.common.req.PageReq;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TrainSeatQueryReq extends PageReq {

    private String trainCode;

    @Override
    public String toString() {
        return "TrainSeatQueryReq{" +
                "trainCode='" + trainCode + '\'' +
                "} " + super.toString();
    }

}