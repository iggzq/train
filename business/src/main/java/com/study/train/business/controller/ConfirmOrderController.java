package com.study.train.business.controller;

import com.study.train.business.req.ConfirmOrderReq;
import com.study.train.business.service.BeforeConfirmOrderService;
import com.study.train.business.service.ConfirmOrderService;
import com.study.train.common.resp.CommonResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/confirm-order")
public class ConfirmOrderController {

    @Resource
    private BeforeConfirmOrderService beforeConfirmOrderService;

    @Resource
    private ConfirmOrderService confirmOrderService;


    @PostMapping("/save-order")
    public CommonResp<Object> saveOrder(@Valid @RequestBody ConfirmOrderReq req) {
        Long confirmOrderId = beforeConfirmOrderService.beforeSaveConfirmOrder(req);
        return new CommonResp<>(confirmOrderId.toString());
    }

    @GetMapping("/query-line-count")
    public CommonResp<Integer> queryLineCount(@RequestParam Long id) {
        Integer count = confirmOrderService.queryLineCount(id);
        return new CommonResp<>(count);
    }

    /*
      @return 秒为单位
     */
//    @PostMapping("/get-expire-time")
//    public CommonResp<Long> getExpireTime(@Valid @RequestBody ConfirmOrderReq confirmOrderReq) {
//        Long expireTime = confirmOrderService.getExpireTime(confirmOrderReq);
//        return new CommonResp<>(expireTime);
//    }


}
