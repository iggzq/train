package com.study.train.business.controller.admin;

import com.study.train.business.dto.DailyTrainQueryDTO;
import com.study.train.business.dto.DailyTrainSaveDTO;
import com.study.train.business.resp.DailyTrainQueryResp;
import com.study.train.business.service.DailyTrainService;
import com.study.train.common.resp.CommonResp;
import com.study.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/admin/daily-train")
public class DailyTrainAdminController {

    @Resource
    private DailyTrainService dailyTrainService;


    @PostMapping("/save")
    public CommonResp<Object> register(@Valid @RequestBody DailyTrainSaveDTO DailyTrainSaveDTO) {
        dailyTrainService.save(DailyTrainSaveDTO);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainQueryResp>> queryList(@Valid DailyTrainQueryDTO DailyTrainQueryDTO) {
        PageResp<DailyTrainQueryResp> list = dailyTrainService.queryList(DailyTrainQueryDTO);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        dailyTrainService.delete(id);
        return new CommonResp<>();
    }

    @GetMapping("/gen-daily/{date}")
    public CommonResp<Object> genDaily(
            @PathVariable
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {

        System.out.println(date.toString());

        dailyTrainService.genDaily(date);
        return new CommonResp<>();
    }


}
