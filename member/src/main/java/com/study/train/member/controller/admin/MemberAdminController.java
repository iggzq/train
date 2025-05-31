package com.study.train.member.controller.admin;

import cn.hutool.core.bean.BeanUtil;
import com.study.train.common.resp.CommonResp;
import com.study.train.common.utils.SnowUtil;
import com.study.train.member.domain.Member;
import com.study.train.member.mapper.MemberMapper;
import com.study.train.member.req.MemberPermissionReq;
import com.study.train.member.req.MemberSaveReq;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/member")
public class MemberAdminController {

    @Resource
    private MemberMapper memberMapper;

    @GetMapping("/query-list")
    public CommonResp<List<Member>> queryList() {
        List<Member> members = memberMapper.selectByExample(null);
        return new CommonResp<>(members);
    }

    @PostMapping("/changePermission")
    public CommonResp<String> changePermission(@RequestBody MemberPermissionReq req) {
        Member member = BeanUtil.copyProperties(req, Member.class);
        memberMapper.updateByPrimaryKeySelective(member);
        return new CommonResp<>("修改成功");
    }

    @PostMapping("/save")
    public CommonResp<String> save(@RequestBody MemberSaveReq req) {
        Member member = BeanUtil.copyProperties(req, Member.class);
        long snowflakeNextId = SnowUtil.getSnowflakeNextId();
        member.setId(snowflakeNextId);
        memberMapper.insert(member);
        return new CommonResp<>("保存成功");
    }
}
