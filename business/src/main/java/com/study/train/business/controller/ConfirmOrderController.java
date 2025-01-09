package com.study.train.business.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.study.train.business.req.ConfirmOrderReq;
import com.study.train.business.req.TicketPayDTO;
import com.study.train.business.service.ConfirmOrderService;
import com.study.train.common.resp.CommonResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/confirm-order")
public class ConfirmOrderController {

    @Resource
    private ConfirmOrderService confirmOrderService;


    @PostMapping("/save-order")
    public CommonResp<TicketPayDTO> saveOrder(@Valid @RequestBody ConfirmOrderReq confirmOrderReq) throws JsonProcessingException {
        TicketPayDTO ticketPayDTO = confirmOrderService.saveConfirm(confirmOrderReq);
        return new CommonResp<>(ticketPayDTO);
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
