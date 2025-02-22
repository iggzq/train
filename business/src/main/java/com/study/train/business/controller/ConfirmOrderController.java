package com.study.train.business.controller;

import com.study.train.business.req.ConfirmOrderReq;
import com.study.train.business.service.BeforeConfirmOrderService;
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
    private BeforeConfirmOrderService beforeConfirmOrderService;


    @PostMapping("/save-order")
    public CommonResp<Object> saveOrder(@Valid @RequestBody ConfirmOrderReq req) {
        beforeConfirmOrderService.beforeSaveConfirmOrder(req);
        return new CommonResp<>();
    }

    /**
     * @return 秒为单位
     */
//    @PostMapping("/get-expire-time")
//    public CommonResp<Long> getExpireTime(@Valid @RequestBody ConfirmOrderReq confirmOrderReq) {
//        Long expireTime = confirmOrderService.getExpireTime(confirmOrderReq);
//        return new CommonResp<>(expireTime);
//    }


}
