package com.study.train.member.enums;

public enum PassengerTypeEnum {

    ADULT("1","成人"),
    CHILD("2","儿童"),

    STUDENT("3","学生");

    private String key;

    private String value;

    public String getKey() {
        return key;
    }

    PassengerTypeEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return "PassengerTypeEnum{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
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
}
