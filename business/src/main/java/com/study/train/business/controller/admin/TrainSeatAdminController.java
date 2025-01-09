package com.study.train.business.controller.admin;

import com.study.train.business.req.TrainSeatQueryReq;
import com.study.train.business.req.TrainSeatSaveReq;
import com.study.train.business.resp.TrainSeatQueryResp;
import com.study.train.business.service.TrainSeatService;
import com.study.train.common.resp.CommonResp;
import com.study.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/train-seat")
public class TrainSeatAdminController {

    @Resource
    private TrainSeatService TrainSeatService;


    @PostMapping("/save")
    public CommonResp<Object> register(@Valid @RequestBody TrainSeatSaveReq TrainSeatSaveReq) {
        TrainSeatService.save(TrainSeatSaveReq);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<TrainSeatQueryResp>> queryList(@Valid TrainSeatQueryReq TrainSeatQueryReq) {
        PageResp<TrainSeatQueryResp> list = TrainSeatService.queryList(TrainSeatQueryReq);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        TrainSeatService.delete(id);
        return new CommonResp<>();
    }


}
