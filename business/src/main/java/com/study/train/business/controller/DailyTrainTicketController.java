package com.study.train.business.controller;

import com.study.train.business.req.DailyTrainTicketQueryReq;
import com.study.train.business.resp.DailyTrainTicketQueryResp;
import com.study.train.business.service.DailyTrainTicketService;
import com.study.train.common.resp.CommonResp;
import com.study.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/daily-train-ticket")
public class DailyTrainTicketController {

    @Resource
    private DailyTrainTicketService DailyTrainTicketService;



    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainTicketQueryResp>> queryList(@Valid DailyTrainTicketQueryReq DailyTrainTicketQueryReq) {
        PageResp<DailyTrainTicketQueryResp> list = DailyTrainTicketService.queryList(DailyTrainTicketQueryReq);
        return new CommonResp<>(list);
    }



}
