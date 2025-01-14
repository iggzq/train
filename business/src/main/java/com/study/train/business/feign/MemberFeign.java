package com.study.train.business.feign;


import com.study.train.common.req.MemberTicketReq;
import com.study.train.common.resp.CommonResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

//@FeignClient(name = "member",url = "http://localhost:8001")
@FeignClient(name = "member")
public interface MemberFeign {
    @PostMapping("/member/feign/ticket/save")
    CommonResp<Object> save(@RequestBody MemberTicketReq memberTicketReq);

    @PostMapping("/member/feign/ticket/update")
    CommonResp<Object> update(@RequestBody MemberTicketReq memberTicketReq);
}
