package com.study.train.business.dto;

import com.study.train.common.req.PageReq;

public class DailyTrainStationSeatQueryDTO extends PageReq {

    private String trainCode;

    @Override
    public String toString() {
        return "DailyTrainStationSeatQueryDTO{" +
                "trainCode='" + trainCode + '\'' +
                "} " + super.toString();
    }

    public String getTrainCode() {
        return trainCode;
    }

    public void setTrainCode(String trainCode) {
        this.trainCode = trainCode;
    }
}