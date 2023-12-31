package com.study.train.member.controller;

import com.study.train.common.resp.CommonResp;
import com.study.train.member.dto.MemberLoginDTO;
import com.study.train.member.dto.MemberRegisterDTO;
import com.study.train.member.dto.MemberSendCodeDTO;
import com.study.train.member.resp.MemberLoginResp;
import com.study.train.member.service.MemberService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

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
    public CommonResp<Long> register(@Valid @RequestBody MemberRegisterDTO memberRegisterDTO) {
        Long registered = memberService.register(memberRegisterDTO);
        return new CommonResp<>(registered);
    }

    @PostMapping("/send-code")
    public CommonResp<String> register(@Valid @RequestBody MemberSendCodeDTO memberSendCodeDTO) {
        return new CommonResp<>(memberService.sendCode(memberSendCodeDTO));
    }

    @PostMapping("/login")
    public CommonResp<MemberLoginResp> login(@Valid @RequestBody MemberLoginDTO memberLoginDTO) {
        return new CommonResp<>(memberService.login(memberLoginDTO));
    }
}
