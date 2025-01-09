package com.study.train.business.controller.admin;

import com.study.train.common.resp.CommonResp;
import com.study.train.common.resp.PageResp;
import com.study.train.business.req.DailyTrainStationSeatQueryReq;
import com.study.train.business.req.DailyTrainStationSeatSaveReq;
import com.study.train.business.resp.DailyTrainStationSeatQueryResp;
import com.study.train.business.service.DailyTrainStationSeatService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/daily-train-seat")
public class DailyTrainStationSeatAdminController {

    @Resource
    private DailyTrainStationSeatService DailyTrainStationSeatService;


    @PostMapping("/save")
    public CommonResp<Object> register(@Valid @RequestBody DailyTrainStationSeatSaveReq DailyTrainStationSeatSaveReq) {
        DailyTrainStationSeatService.save(DailyTrainStationSeatSaveReq);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainStationSeatQueryResp>> queryList(@Valid DailyTrainStationSeatQueryReq DailyTrainStationSeatQueryReq) {
        PageResp<DailyTrainStationSeatQueryResp> list = DailyTrainStationSeatService.queryList(DailyTrainStationSeatQueryReq);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        DailyTrainStationSeatService.delete(id);
        return new CommonResp<>();
    }


}
