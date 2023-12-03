package com.study.train.business.controller.admin;

import com.study.train.business.dto.TrainSeatQueryDTO;
import com.study.train.business.dto.TrainSeatSaveDTO;
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
    public CommonResp<Object> register(@Valid @RequestBody TrainSeatSaveDTO TrainSeatSaveDTO) {
        TrainSeatService.save(TrainSeatSaveDTO);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<TrainSeatQueryResp>> queryList(@Valid TrainSeatQueryDTO TrainSeatQueryDTO) {
        PageResp<TrainSeatQueryResp> list = TrainSeatService.queryList(TrainSeatQueryDTO);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        TrainSeatService.delete(id);
        return new CommonResp<>();
    }


}
