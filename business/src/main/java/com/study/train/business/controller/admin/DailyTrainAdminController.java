package com.study.train.business.controller.admin;

import com.study.train.common.context.LoginMemberContext;
import com.study.train.common.resp.CommonResp;
import com.study.train.common.resp.PageResp;
import com.study.train.business.dto.DailyTrainQueryDTO;
import com.study.train.business.dto.DailyTrainSaveDTO;
import com.study.train.business.resp.DailyTrainQueryResp;
import com.study.train.business.service.DailyTrainService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/daily-train")
public class DailyTrainAdminController {

    @Resource
    private DailyTrainService DailyTrainService;


    @PostMapping("/save")
    public CommonResp<Object> register(@Valid @RequestBody DailyTrainSaveDTO DailyTrainSaveDTO) {
        DailyTrainService.save(DailyTrainSaveDTO);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainQueryResp>> queryList(@Valid DailyTrainQueryDTO DailyTrainQueryDTO) {
        PageResp<DailyTrainQueryResp> list = DailyTrainService.queryList(DailyTrainQueryDTO);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        DailyTrainService.delete(id);
        return new CommonResp<>();
    }


}
