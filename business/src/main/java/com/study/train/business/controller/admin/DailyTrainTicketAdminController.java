package com.study.train.business.controller.admin;

import com.study.train.business.req.DailyTrainTicketQueryReq;
import com.study.train.common.resp.CommonResp;
import com.study.train.common.resp.PageResp;
import com.study.train.business.req.DailyTrainTicketSaveReq;
import com.study.train.business.resp.DailyTrainTicketQueryResp;
import com.study.train.business.service.DailyTrainTicketService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/daily-train-ticket")
public class DailyTrainTicketAdminController {

    @Resource
    private DailyTrainTicketService DailyTrainTicketService;


    @PostMapping("/save")
    public CommonResp<Object> register(@Valid @RequestBody DailyTrainTicketSaveReq DailyTrainTicketSaveReq) {
        DailyTrainTicketService.save(DailyTrainTicketSaveReq);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainTicketQueryResp>> queryList(@Valid DailyTrainTicketQueryReq DailyTrainTicketQueryReq) {
        PageResp<DailyTrainTicketQueryResp> list = DailyTrainTicketService.queryList(DailyTrainTicketQueryReq);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        DailyTrainTicketService.delete(id);
        return new CommonResp<>();
    }


}
