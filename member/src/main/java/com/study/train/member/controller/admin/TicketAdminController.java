package com.study.train.member.controller.admin;

import com.study.train.common.resp.CommonResp;
import com.study.train.common.resp.PageResp;
import com.study.train.member.dto.TicketQueryDTO;
import com.study.train.member.resp.TicketQueryResp;
import com.study.train.member.service.TicketService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/ticket")
public class TicketAdminController {

    @Resource
    private TicketService TicketService;



    @GetMapping("/query-list")
    public CommonResp<PageResp<TicketQueryResp>> queryList(@Valid TicketQueryDTO ticketQueryDTO) {
        PageResp<TicketQueryResp> list = TicketService.queryList(ticketQueryDTO);
        return new CommonResp<>(list);
    }


}
