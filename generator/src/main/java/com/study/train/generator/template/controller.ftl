package com.study.train.${module}.controller;

import com.study.train.common.context.LoginMemberContext;
import com.study.train.common.resp.CommonResp;
import com.study.train.common.resp.PageResp;
import com.study.train.${module}.req.${Domain}QueryReq;
import com.study.train.${module}.req.${Domain}SaveReq;
import com.study.train.${module}.resp.${Domain}QueryResp;
import com.study.train.${module}.service.${Domain}Service;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/${do_main}")
public class ${Domain}Controller {

    @Resource
    private ${Domain}Service ${Domain}Service;


    @PostMapping("/save")
    public CommonResp<Object> register(@Valid @RequestBody ${Domain}SaveReq ${Domain}SaveReq) {
        ${Domain}Service.save(${Domain}SaveReq);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<${Domain}QueryResp>> queryList(@Valid ${Domain}QueryReq ${Domain}QueryReq) {
        PageResp<${Domain}QueryResp> list = ${Domain}Service.queryList(${Domain}QueryReq);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        ${Domain}Service.delete(id);
        return new CommonResp<>();
    }


}
