package com.study.train.business.controller.admin;

import com.study.train.business.dto.StationQueryDTO;
import com.study.train.business.dto.StationSaveDTO;
import com.study.train.business.resp.StationQueryResp;
import com.study.train.business.service.StationService;
import com.study.train.common.resp.CommonResp;
import com.study.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/station")
public class StationAdminController {

    @Resource
    private StationService StationService;


    @PostMapping("/save")
    public CommonResp<Object> register(@Valid @RequestBody StationSaveDTO StationSaveDTO) {
        StationService.save(StationSaveDTO);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<StationQueryResp>> queryList(@Valid StationQueryDTO StationQueryDTO) {
        PageResp<StationQueryResp> list = StationService.queryList(StationQueryDTO);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        StationService.delete(id);
        return new CommonResp<>();
    }


}
