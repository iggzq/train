package com.study.train.business.controller.admin;

import com.study.train.business.dto.TrainStationQueryDTO;
import com.study.train.business.dto.TrainStationSaveDTO;
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
    public CommonResp<Object> register(@Valid @RequestBody TrainStationSaveDTO TrainStationSaveDTO) {
        TrainStationService.save(TrainStationSaveDTO);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<TrainStationQueryResp>> queryList(@Valid TrainStationQueryDTO TrainStationQueryDTO) {
        PageResp<TrainStationQueryResp> list = TrainStationService.queryList(TrainStationQueryDTO);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        TrainStationService.delete(id);
        return new CommonResp<>();
    }


}
