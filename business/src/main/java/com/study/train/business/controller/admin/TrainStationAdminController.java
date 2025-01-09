package com.study.train.business.controller.admin;

import com.study.train.business.req.TrainStationQueryReq;
import com.study.train.business.req.TrainStationSaveReq;
import com.study.train.business.resp.TrainStationQueryResp;
import com.study.train.business.service.TrainStationService;
import com.study.train.common.resp.CommonResp;
import com.study.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/train-station")
public class TrainStationAdminController {

    @Resource
    private TrainStationService TrainStationService;


    @PostMapping("/save")
    public CommonResp<Object> register(@Valid @RequestBody TrainStationSaveReq TrainStationSaveReq) {
        TrainStationService.save(TrainStationSaveReq);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<TrainStationQueryResp>> queryList(@Valid TrainStationQueryReq TrainStationQueryReq) {
        PageResp<TrainStationQueryResp> list = TrainStationService.queryList(TrainStationQueryReq);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        TrainStationService.delete(id);
        return new CommonResp<>();
    }


}
