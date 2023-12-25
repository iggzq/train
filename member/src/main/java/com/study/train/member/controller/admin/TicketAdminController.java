package com.study.train.member.controller.admin;

import com.study.train.common.context.LoginMemberContext;
import com.study.train.common.resp.CommonResp;
import com.study.train.common.resp.PageResp;
import com.study.train.member.dto.TicketQueryDTO;
import com.study.train.member.dto.TicketSaveDTO;
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


    @PostMapping("/save")
    public CommonResp<Object> register(@Valid @RequestBody TicketSaveDTO TicketSaveDTO) {
        TicketService.save(TicketSaveDTO);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<TicketQueryResp>> queryList(@Valid TicketQueryDTO TicketQueryDTO) {
        PageResp<TicketQueryResp> list = TicketService.queryList(TicketQueryDTO);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        TicketService.delete(id);
        return new CommonResp<>();
    }


}
