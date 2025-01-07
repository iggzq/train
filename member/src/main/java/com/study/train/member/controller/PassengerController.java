package com.study.train.member.controller;

import com.study.train.common.context.LoginMemberHolder;
import com.study.train.common.resp.CommonResp;
import com.study.train.common.resp.PageResp;
import com.study.train.member.dto.PassengerQueryDTO;
import com.study.train.member.req.PassengerSaveReq;
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
    private LoginMemberHolder loginMemberHolder;

    @Resource
    private PassengerService PassengerService;


    @PostMapping("/save")
    public CommonResp<Object> register(@Valid @RequestBody PassengerSaveReq passengerSaveReq) {
        PassengerService.save(passengerSaveReq);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<PassengerQueryResp>> queryList(@Valid PassengerQueryDTO passengerQueryDTO) {
        passengerQueryDTO.setMemberId(loginMemberHolder.getId());
        PageResp<PassengerQueryResp> list = PassengerService.queryList(passengerQueryDTO);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        PassengerService.delete(id);
        return new CommonResp<>();
    }

    @GetMapping("/query-my-passenger")
    public CommonResp<List<PassengerQueryResp>> queryMyPassenger() {
        List<PassengerQueryResp> passengerQueryResps = PassengerService.queryMyPassenger();
        return new CommonResp<>(passengerQueryResps);
    }

    @GetMapping("/hello")
    public String hello() {
        return ("hello world  " );
    }

}
