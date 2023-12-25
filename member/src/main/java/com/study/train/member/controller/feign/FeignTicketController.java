package com.study.train.member.controller.feign;

import com.study.train.common.req.MemberTicketReq;
import com.study.train.common.resp.CommonResp;
import com.study.train.member.service.TicketService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feign/ticket")
public class FeignTicketController {

    @Resource
    private TicketService ticketService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody MemberTicketReq memberTicketReq){
        ticketService.save(memberTicketReq);
        return new CommonResp<>();
    }
}
