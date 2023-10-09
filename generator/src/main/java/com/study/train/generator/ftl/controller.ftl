package com.study.train.member.controller;

import com.study.train.common.context.LoginMemberContext;
import com.study.train.common.resp.CommonResp;
import com.study.train.member.dto.PageDTO;
import com.study.train.member.dto.${Domain}QueryDTO;
import com.study.train.member.dto.${Domain}SaveDTO;
import com.study.train.member.resp.${Domain}QueryResp;
import com.study.train.member.service.${Domain}Service;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/${Domain}")
public class ${Domain}Controller {

    @Resource
    private ${Domain}Service ${Domain}Service;


    @PostMapping("/save")
    public CommonResp<Object> register(@Valid @RequestBody ${Domain}SaveDTO ${Domain}SaveDTO) {
        ${Domain}Service.save(${Domain}SaveDTO);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageDTO<${Domain}QueryResp>> queryList(@Valid ${Domain}QueryDTO ${Domain}QueryDTO) {
        ${Domain}QueryDTO.setMemberId(LoginMemberContext.getId());
        PageDTO<${Domain}QueryResp> list = ${Domain}Service.queryList(${Domain}QueryDTO);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        ${Domain}Service.delete(id);
        return new CommonResp<>();
    }


}
