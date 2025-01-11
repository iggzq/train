package com.study.train.business.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.study.train.business.domain.ConfirmOrder;
import com.study.train.business.req.TicketPayReq;
import com.study.train.business.mapper.ConfirmOrderMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ticket-pay/")
public class AliPayController {

    @Resource
    private AlipayClient alipayClient;
    @Resource
    private AlipayTradePagePayRequest alipayTradePagePayRequest;

    @Resource
    private ConfirmOrderMapper confirmOrderMapper;

    //处理支付宝支付
    @RequestMapping("pay")
    public String pay(@RequestBody TicketPayReq ticketPayReq) throws AlipayApiException {
        String tradeNum = ticketPayReq.getTradeNum();
        Long ticketId = Long.valueOf(tradeNum);
        ConfirmOrder confirmOrder = confirmOrderMapper.selectByPrimaryKey(ticketId);
        System.out.println(confirmOrder);
        String amount = String.valueOf(confirmOrder.getAmount());
        System.out.println( "{\"out_trade_no\":\"" + ticketPayReq.getTradeNum() + "\","
                + "\"total_amount\":\"" + amount + "\","
                + "\"subject\":\"" + ticketPayReq.getTradeName() + "\","
                + "\"body\":\"" + ticketPayReq.getSubject() + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        alipayTradePagePayRequest.setNotifyUrl("www.baidu.com");
        alipayTradePagePayRequest.setReturnUrl("www.bilibili.com");
        alipayTradePagePayRequest.setBizContent(
                "{\"out_trade_no\":\"" + ticketPayReq.getTradeNum() + "\","
                        + "\"total_amount\":\"" + amount + "\","
                        + "\"subject\":\"" + ticketPayReq.getTradeName() + "\","
                        + "\"body\":\"" + ticketPayReq.getSubject() + "\","
                        + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        return alipayClient.pageExecute(alipayTradePagePayRequest).getBody();

    }

}
