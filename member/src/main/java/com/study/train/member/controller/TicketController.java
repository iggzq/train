package com.study.train.member.controller;

import com.study.train.common.context.LoginMemberHolder;
import com.study.train.common.resp.CommonResp;
import com.study.train.common.resp.PageResp;
import com.study.train.member.req.TicketQueryReq;
import com.study.train.member.req.TicketUpdatePublicReq;
import com.study.train.member.resp.TicketQueryResp;
import com.study.train.member.service.TicketService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ticket")
public class TicketController {

    @Resource
    private TicketService ticketService;

    @Resource
    private LoginMemberHolder loginMemberHolder;

    @GetMapping("/query-list")
    public CommonResp<PageResp<TicketQueryResp>> query(@Valid TicketQueryReq req) {
        CommonResp<PageResp<TicketQueryResp>> commonResp = new CommonResp<>();
        req.setMemberId(loginMemberHolder.getId());
        PageResp<TicketQueryResp> pageResp = ticketService.queryList(req);
        commonResp.setContent(pageResp);
        return commonResp;
    }

    @PostMapping("/changePublicShow")
    public CommonResp<String> changePublicShow(@RequestBody TicketUpdatePublicReq ticket) {
        ticketService.changePublicShow(ticket);
        return new CommonResp<>("修改成功");
    }

}
