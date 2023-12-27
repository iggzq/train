package com.study.train.business.feign;


import com.study.train.common.req.MemberTicketReq;
import com.study.train.common.resp.CommonResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "member",url = "http://localhost:8001")
public interface MemberFeign {
    @GetMapping("/member/feign/ticket/save")
    CommonResp<Object> save(@RequestBody MemberTicketReq memberTicketReq);

    @GetMapping("/member/feign/ticket/update")
    CommonResp<Object> update(@RequestBody MemberTicketReq memberTicketReq);
}
