package com.study.train.${module}.controller.admin;

import com.study.train.common.context.LoginMemberContext;
import com.study.train.common.resp.CommonResp;
import com.study.train.common.resp.PageResp;
import com.study.train.${module}.dto.${Domain}QueryDTO;
import com.study.train.${module}.dto.${Domain}SaveDTO;
import com.study.train.${module}.resp.${Domain}QueryResp;
import com.study.train.${module}.service.${Domain}Service;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/${do_main}")
public class ${Domain}AdminController {

    @Resource
    private ${Domain}Service ${Domain}Service;


    @PostMapping("/save")
    public CommonResp<Object> register(@Valid @RequestBody ${Domain}SaveDTO ${Domain}SaveDTO) {
        ${Domain}Service.save(${Domain}SaveDTO);
        return new CommonResp<>();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<${Domain}QueryResp>> queryList(@Valid ${Domain}QueryDTO ${Domain}QueryDTO) {
        PageResp<${Domain}QueryResp> list = ${Domain}Service.queryList(${Domain}QueryDTO);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        ${Domain}Service.delete(id);
        return new CommonResp<>();
    }


}
