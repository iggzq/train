package com.study.train.member.controller.admin;

import com.study.train.common.resp.CommonResp;
import com.study.train.common.resp.PageResp;
import com.study.train.member.req.TicketQueryReq;
import com.study.train.member.resp.TicketQueryResp;
import com.study.train.member.service.TicketService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/ticket")
public class TicketAdminController {

    @Resource
    private TicketService ticketService;



    @GetMapping("/query-list")
    public CommonResp<PageResp<TicketQueryResp>> queryList(@Valid TicketQueryReq ticketQueryReq) {
        PageResp<TicketQueryResp> list = ticketService.queryList(ticketQueryReq);
        return new CommonResp<>(list);
    }

    @GetMapping("/query-list-status")
    public CommonResp<PageResp<TicketQueryResp>> queryListStatus(@Valid TicketQueryReq ticketQueryReq) {
        PageResp<TicketQueryResp> list = ticketService.queryListStatus(ticketQueryReq);
        return new CommonResp<>(list);
    }


}
