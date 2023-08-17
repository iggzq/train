package com.study.train.member.controller;

import com.study.train.common.resp.CommonResp;
import com.study.train.member.dto.MemberRegisterDTO;
import com.study.train.member.dto.MemberSendCodeDTO;
import com.study.train.member.service.MemberService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
public class MemberController {

    @Resource
    private MemberService memberService;

    @GetMapping("/count")
    public Integer count() {
        return memberService.count();
    }

    @PostMapping("/register")
    public CommonResp<Long> register(@Valid MemberRegisterDTO memberRegisterDTO) {
        Long registered = memberService.register(memberRegisterDTO);
        return new CommonResp<>(registered);
    }

    @PostMapping("/send-code")
    public CommonResp<String> register(@Valid MemberSendCodeDTO memberSendCodeDTO) {
        return new CommonResp<>(memberService.sendCode(memberSendCodeDTO));
    }
}
