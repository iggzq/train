package com.study.train.member.dto;

import java.io.Serializable;
import java.util.List;

public class PageDTO<T> implements Serializable {

    private Long total;

    private List<T> data;


    @Override
    public String toString() {
        return "PageDTO{" +
                "total=" + total +
                ", data=" + data +
                '}';
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }



}
