package com.study.train.business.controller;

import com.study.train.common.context.LoginMemberContext;
import com.study.train.common.resp.CommonResp;
import com.study.train.business.dto.PageDTO;
import com.study.train.business.dto.StationQueryDTO;
import com.study.train.business.dto.StationSaveDTO;
import com.study.train.business.resp.StationQueryResp;
import com.study.train.business.service.StationService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Station")
public class StationController {

    @Resource
    private StationService StationService;


    @PostMapping("/save")
    public CommonResp<Object> register(@Valid @RequestBody StationSaveDTO StationSaveDTO) {
        StationService.save(StationSaveDTO);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageDTO<StationQueryResp>> queryList(@Valid StationQueryDTO StationQueryDTO) {
        PageDTO<StationQueryResp> list = StationService.queryList(StationQueryDTO);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        StationService.delete(id);
        return new CommonResp<>();
    }


}
