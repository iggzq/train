package com.study.train.member.controller;

import com.study.train.common.context.LoginMemberContext;
import com.study.train.common.resp.CommonResp;
import com.study.train.member.dto.PassengerQueryDTO;
import com.study.train.member.dto.PassengerSaveDTO;
import com.study.train.member.resp.PassengerQueryResp;
import com.study.train.member.service.PassengerService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/passenger")
public class PassengerController {

    @Resource
    private PassengerService passengerService;


    @PostMapping("/save")
    public CommonResp<Object> register(@Valid @RequestBody PassengerSaveDTO passengerSaveDTO) {
        passengerService.save(passengerSaveDTO);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<List<PassengerQueryResp>> queryList(@Valid PassengerQueryDTO passengerQueryDTO) {
        passengerQueryDTO.setMemberId(LoginMemberContext.getId());
        List<PassengerQueryResp> list = passengerService.queryList(passengerQueryDTO);
        return new CommonResp<>(list);
    }

}
