package com.study.train.business.controller.admin;

import com.study.train.business.req.DailyTrainStationCarriageQueryReq;
import com.study.train.business.req.DailyTrainStationCarriageSaveReq;
import com.study.train.business.resp.DailyTrainStationCarriageQueryResp;
import com.study.train.business.service.DailyTrainStationCarriageService;
import com.study.train.common.resp.CommonResp;
import com.study.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/daily-train-carriage")
public class DailyTrainStationCarriageAdminController {

    @Resource
    private DailyTrainStationCarriageService DailyTrainStationCarriageService;


    @PostMapping("/save")
    public CommonResp<Object> register(@Valid @RequestBody DailyTrainStationCarriageSaveReq DailyTrainStationCarriageSaveReq) {
        DailyTrainStationCarriageService.save(DailyTrainStationCarriageSaveReq);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainStationCarriageQueryResp>> queryList(@Valid DailyTrainStationCarriageQueryReq DailyTrainStationCarriageQueryReq) {
        PageResp<DailyTrainStationCarriageQueryResp> list = DailyTrainStationCarriageService.queryList(DailyTrainStationCarriageQueryReq);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        DailyTrainStationCarriageService.delete(id);
        return new CommonResp<>();
    }


}
