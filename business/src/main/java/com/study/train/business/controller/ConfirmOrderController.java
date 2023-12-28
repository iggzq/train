package com.study.train.business.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.study.train.business.dto.ConfirmOrderDTO;
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
    public CommonResp<Float> saveOrder(@Valid @RequestBody ConfirmOrderDTO confirmOrderDTO) throws JsonProcessingException {
        Float totalMoney = confirmOrderService.saveConfirm(confirmOrderDTO);
        return new CommonResp<>(totalMoney);
    }

    /**
     *
     * @return 秒为单位
     */
    @PostMapping("/get-expire-time")
    public CommonResp<Long> getExpireTime(@Valid @RequestBody ConfirmOrderDTO confirmOrderDTO) {
        Long expireTime = confirmOrderService.getExpireTime(confirmOrderDTO);
        System.out.println(expireTime);
        return new CommonResp<>(expireTime);
    }




}
