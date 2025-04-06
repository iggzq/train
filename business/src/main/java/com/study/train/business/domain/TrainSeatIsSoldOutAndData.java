package com.study.train.business.domain;

import lombok.Data;

import java.util.List;

@Data
public class TrainSeatIsSoldOutAndData {

    private Boolean isSoldOut;
    private List<DailyTrainStationSeat> list;
}
