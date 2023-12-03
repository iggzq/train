package com.study.train.business.enums;

import java.math.BigDecimal;

public enum TrainTypeEnum {

    G("G","高铁",new BigDecimal("1.2")),
    D("D","动车",new BigDecimal("1")),
    K("K","快速",new BigDecimal("0.8"));

    TrainTypeEnum(String key, String value, BigDecimal priceRate) {
        this.key = key;
        this.value = value;
        this.priceRate = priceRate;
    }

    private String key;

    private String value;

    private BigDecimal priceRate;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "TrainTypeEnum{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", priceRate=" + priceRate +
                '}';
    }

    public BigDecimal getPriceRate() {
        return priceRate;
    }

    public void setPriceRate(BigDecimal priceRate) {
        this.priceRate = priceRate;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
