package com.study.train.business.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.study.train.business.dto.ConfirmOrderDTO;
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
    private ConfirmOrderService ConfirmOrderService;


    @PostMapping("/save-order")
    public CommonResp<Float> saveOrder(@Valid @RequestBody ConfirmOrderDTO confirmOrderDTO) throws JsonProcessingException {
        Float totalMoney = ConfirmOrderService.saveConfirm(confirmOrderDTO);
        return new CommonResp<>(totalMoney);
    }




}
