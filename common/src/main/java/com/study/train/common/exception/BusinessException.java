package com.study.train.common.exception;

public class BusinessException extends RuntimeException{

    private BusinessExceptionEnum businessExceptionEnum;


    public BusinessException(BusinessExceptionEnum businessExceptionEnum) {
        this.businessExceptionEnum = businessExceptionEnum;
    }

    @Override
    public String toString() {
        return "BussinessException{" +
                "businessExceptionEnum=" + businessExceptionEnum +
                '}';
    }

    public BusinessExceptionEnum getBusinessExceptionEnum() {
        return businessExceptionEnum;
    }

    public void setBusinessExceptionEnum(BusinessExceptionEnum businessExceptionEnum) {
        this.businessExceptionEnum = businessExceptionEnum;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
