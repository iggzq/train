package com.study.train.business.controller.admin;

import com.study.train.business.req.TrainCarriageQueryReq;
import com.study.train.business.req.TrainCarriageSaveReq;
import com.study.train.business.resp.TrainCarriageQueryResp;
import com.study.train.business.service.TrainCarriageService;
import com.study.train.common.resp.CommonResp;
import com.study.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/train-carriage")
public class TrainCarriageAdminController {

    @Resource
    private TrainCarriageService TrainCarriageService;


    @PostMapping("/save")
    public CommonResp<Object> register(@Valid @RequestBody TrainCarriageSaveReq TrainCarriageSaveReq) {
        TrainCarriageService.save(TrainCarriageSaveReq);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<TrainCarriageQueryResp>> queryList(@Valid TrainCarriageQueryReq TrainCarriageQueryReq) {
        PageResp<TrainCarriageQueryResp> list = TrainCarriageService.queryList(TrainCarriageQueryReq);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        TrainCarriageService.delete(id);
        return new CommonResp<>();
    }


}
