package com.study.train.business.controller.admin;

import com.study.train.business.req.TrainQueryReq;
import com.study.train.business.req.TrainSaveReq;
import com.study.train.business.resp.TrainQueryResp;
import com.study.train.business.service.TrainSeatService;
import com.study.train.business.service.TrainService;
import com.study.train.common.resp.CommonResp;
import com.study.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/train")
public class TrainAdminController {

    @Resource
    private TrainService trainService;

    @Resource
    private TrainSeatService trainSeatService;


    @PostMapping("/save")
    public CommonResp<Object> register(@Valid @RequestBody TrainSaveReq TrainSaveReq) {
        trainService.save(TrainSaveReq);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<TrainQueryResp>> queryList(@Valid TrainQueryReq TrainQueryReq) {
        PageResp<TrainQueryResp> list = trainService.queryList(TrainQueryReq);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        trainService.delete(id);
        return new CommonResp<>();
    }

    @GetMapping("/query-all")
    public CommonResp<List<TrainQueryResp>> queryAll() {
        List<TrainQueryResp> list = trainService.queryAll();
        return new CommonResp<>(list);
    }

    @PostMapping("/gen-seat/{trainCode}")
    public CommonResp<Object> genSeat(@Valid @PathVariable String trainCode) {
        trainSeatService.genTrainSeat(trainCode);
        return new CommonResp<>();
    }




}
