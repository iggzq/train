package com.study.train.business.controller.admin;

import com.study.train.common.resp.CommonResp;
import com.study.train.common.resp.PageResp;
import com.study.train.business.req.DailyTrainStationQueryReq;
import com.study.train.business.req.DailyTrainStationSaveReq;
import com.study.train.business.resp.DailyTrainStationQueryResp;
import com.study.train.business.service.DailyTrainStationService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/daily-train-station")
public class DailyTrainStationAdminController {

    @Resource
    private DailyTrainStationService DailyTrainStationService;


    @PostMapping("/save")
    public CommonResp<Object> register(@Valid @RequestBody DailyTrainStationSaveReq DailyTrainStationSaveReq) {
        DailyTrainStationService.save(DailyTrainStationSaveReq);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainStationQueryResp>> queryList(@Valid DailyTrainStationQueryReq DailyTrainStationQueryReq) {
        PageResp<DailyTrainStationQueryResp> list = DailyTrainStationService.queryList(DailyTrainStationQueryReq);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        DailyTrainStationService.delete(id);
        return new CommonResp<>();
    }


}
