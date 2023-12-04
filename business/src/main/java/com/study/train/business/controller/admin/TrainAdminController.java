package com.study.train.business.controller.admin;

import com.study.train.business.dto.TrainQueryDTO;
import com.study.train.business.dto.TrainSaveDTO;
import com.study.train.business.resp.TrainQueryResp;
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


    @PostMapping("/save")
    public CommonResp<Object> register(@Valid @RequestBody TrainSaveDTO TrainSaveDTO) {
        trainService.save(TrainSaveDTO);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<TrainQueryResp>> queryList(@Valid TrainQueryDTO TrainQueryDTO) {
        PageResp<TrainQueryResp> list = trainService.queryList(TrainQueryDTO);
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




}
