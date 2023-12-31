package com.study.train.business.enums;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

public enum SeatTypeEnum {
    YDZ("1", "一等座", new BigDecimal("0.4")),
    EDZ("2", "二等座", new BigDecimal("0.3")),
    RW("3", "软卧", new BigDecimal("0.6")),
    YW("4", "硬卧", new BigDecimal("0.5"));

    SeatTypeEnum(String key, String value, BigDecimal price) {
        this.key = key;
        this.value = value;
        this.price = price;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private String key;

    private String value;

    /**
     * 基础票价 N元/公里，0.4即为0.4元/公里
     */
    private BigDecimal price;


    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public static List<HashMap<String,String>> getEnumList() {
        List<HashMap<String, String>> list = new ArrayList<>();
        for (SeatTypeEnum anEnum : EnumSet.allOf(SeatTypeEnum.class)) {
            HashMap<String, String> map = new HashMap<>();
            map.put("code",anEnum.key);
            map.put("desc",anEnum.value);
            list.add(map);
        }
        return list;
    }

    public static SeatTypeEnum getEnumByCode(String code) {
        for (SeatTypeEnum enums : SeatTypeEnum.values()) {
            if (enums.getKey().equalsIgnoreCase(code)) {
                return enums;
            }
        }
        return null;
    }
}
