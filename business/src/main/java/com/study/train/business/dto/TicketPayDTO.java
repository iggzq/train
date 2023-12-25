package com.study.train.business.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TicketPayDTO {

    //订单号
    @NotBlank
    private String tradeNum;
    //订单名称
    @NotBlank
    private String tradeName;
    //金额
    @NotBlank
    private String amount;
    //商品描述
    private String subject;
}
