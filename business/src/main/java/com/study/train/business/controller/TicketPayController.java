package com.study.train.business.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.study.train.business.dto.TicketPayDTO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ticket-pay/")
public class TicketPayController {

    @Resource
    private AlipayClient alipayClient;
    @Resource
    private AlipayTradePagePayRequest alipayTradePagePayRequest;

    //处理支付宝支付
    @RequestMapping("pay")
    public String pay(@RequestBody TicketPayDTO ticketPayDTO) throws AlipayApiException {
        alipayTradePagePayRequest.setNotifyUrl("www.baidu.com");
        alipayTradePagePayRequest.setReturnUrl("www.bilibili.com");
        alipayTradePagePayRequest.setBizContent(
                "{\"out_trade_no\":\""+ ticketPayDTO.getTradeNum() +"\","
                        + "\"total_amount\":\""+ ticketPayDTO.getAmount() +"\","
                        + "\"subject\":\""+ ticketPayDTO.getTradeName() +"\","
                        + "\"body\":\""+ ticketPayDTO.getSubject() +"\","
                        + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        return alipayClient.pageExecute(alipayTradePagePayRequest).getBody();

    }

}
