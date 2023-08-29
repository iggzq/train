package com.study.train.member.controller;

import com.study.train.common.resp.CommonResp;
import com.study.train.member.dto.PassengerSaveDTO;
import com.study.train.member.service.PassengerService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
