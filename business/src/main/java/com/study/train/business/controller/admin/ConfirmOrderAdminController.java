package com.study.train.business.controller.admin;

import com.study.train.common.resp.CommonResp;
import com.study.train.common.resp.PageResp;
import com.study.train.business.dto.ConfirmOrderQueryDTO;
import com.study.train.business.dto.ConfirmOrderSaveDTO;
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
    public CommonResp<Object> register(@Valid @RequestBody ConfirmOrderSaveDTO ConfirmOrderSaveDTO) {
        ConfirmOrderService.save(ConfirmOrderSaveDTO);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<ConfirmOrderQueryResp>> queryList(@Valid ConfirmOrderQueryDTO ConfirmOrderQueryDTO) {
        PageResp<ConfirmOrderQueryResp> list = ConfirmOrderService.queryList(ConfirmOrderQueryDTO);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        ConfirmOrderService.delete(id);
        return new CommonResp<>();
    }


}
