package com.study.train.business.controller;

import com.study.train.business.req.ConfirmOrderReq;
import com.study.train.business.req.TicketPayReq;
import com.study.train.business.service.ConfirmOrderService;
import com.study.train.common.resp.CommonResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/confirm-order")
public class ConfirmOrderController {

    @Resource
    private ConfirmOrderService confirmOrderService;


    @PostMapping("/save-order")
    public CommonResp<TicketPayReq> saveOrder(@Valid @RequestBody ConfirmOrderReq confirmOrderReq) {
        TicketPayReq ticketPayReq = confirmOrderService.saveConfirm(confirmOrderReq);
        return new CommonResp<>(ticketPayReq);
    }

    /**
     *
     * @return 秒为单位
     */
    @PostMapping("/get-expire-time")
    public CommonResp<Long> getExpireTime(@Valid @RequestBody ConfirmOrderReq confirmOrderReq) {
        Long expireTime = confirmOrderService.getExpireTime(confirmOrderReq);
        return new CommonResp<>(expireTime);
    }




}
