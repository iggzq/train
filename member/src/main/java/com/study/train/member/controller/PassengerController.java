package com.study.train.member.controller;

import com.study.train.common.context.LoginMemberContext;
import com.study.train.common.resp.CommonResp;
import com.study.train.member.dto.PageDTO;
import com.study.train.member.dto.PassengerQueryDTO;
import com.study.train.member.dto.PassengerSaveDTO;
import com.study.train.member.resp.PassengerQueryResp;
import com.study.train.member.service.PassengerService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Passenger")
public class PassengerController {

    @Resource
    private PassengerService PassengerService;


    @PostMapping("/save")
    public CommonResp<Object> register(@Valid @RequestBody PassengerSaveDTO PassengerSaveDTO) {
        PassengerService.save(PassengerSaveDTO);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageDTO<PassengerQueryResp>> queryList(@Valid PassengerQueryDTO PassengerQueryDTO) {
        PassengerQueryDTO.setMemberId(LoginMemberContext.getId());
        PageDTO<PassengerQueryResp> list = PassengerService.queryList(PassengerQueryDTO);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        PassengerService.delete(id);
        return new CommonResp<>();
    }


}
