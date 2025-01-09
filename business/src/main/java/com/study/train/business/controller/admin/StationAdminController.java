package com.study.train.business.controller.admin;

import com.study.train.business.req.StationQueryReq;
import com.study.train.business.req.StationSaveReq;
import com.study.train.business.resp.StationQueryResp;
import com.study.train.business.service.StationService;
import com.study.train.common.resp.CommonResp;
import com.study.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/station")
public class StationAdminController {

    @Resource
    private StationService stationService;


    @PostMapping("/save")
    public CommonResp<Object> register(@Valid @RequestBody StationSaveReq StationSaveReq) {
        stationService.save(StationSaveReq);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<StationQueryResp>> queryList(@Valid StationQueryReq StationQueryReq) {
        PageResp<StationQueryResp> list = stationService.queryList(StationQueryReq);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        stationService.delete(id);
        return new CommonResp<>();
    }

    @GetMapping("/query-all")
    public CommonResp<List<StationQueryResp>> queryAll() {
        List<StationQueryResp> list = stationService.queryAll();
        return new CommonResp<>(list);
    }


}
