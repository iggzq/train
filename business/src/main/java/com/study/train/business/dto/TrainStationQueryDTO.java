package com.study.train.business.dto;

import com.study.train.common.req.PageReq;

public class TrainStationQueryDTO extends PageReq {

    private String trainCode;

    @Override
    public String toString() {
        return "TrainStationQueryDTO{" +
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