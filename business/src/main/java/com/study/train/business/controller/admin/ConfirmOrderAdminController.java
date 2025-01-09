package com.study.train.business.controller.admin;

import com.study.train.common.resp.CommonResp;
import com.study.train.common.resp.PageResp;
import com.study.train.business.req.ConfirmOrderQueryReq;
import com.study.train.business.req.ConfirmOrderSaveReq;
import com.study.train.business.resp.ConfirmOrderQueryResp;
import com.study.train.business.service.ConfirmOrderService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/confirm-order")
public class ConfirmOrderAdminController {

    @Resource
    private ConfirmOrderService ConfirmOrderService;


    @PostMapping("/save")
    public CommonResp<Object> register(@Valid @RequestBody ConfirmOrderSaveReq ConfirmOrderSaveReq) {
        ConfirmOrderService.save(ConfirmOrderSaveReq);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<ConfirmOrderQueryResp>> queryList(@Valid ConfirmOrderQueryReq ConfirmOrderQueryReq) {
        PageResp<ConfirmOrderQueryResp> list = ConfirmOrderService.queryList(ConfirmOrderQueryReq);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        ConfirmOrderService.delete(id);
        return new CommonResp<>();
    }


}
