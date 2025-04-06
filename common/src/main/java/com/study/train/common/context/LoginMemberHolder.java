package com.study.train.common.context;

import com.study.train.common.resp.MemberLoginResp;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class LoginMemberHolder {

    private MemberLoginResp member;

    public void setMember(MemberLoginResp member) {
        this.member = member;
    }

    public MemberLoginResp getMember() {
        return member;
    }

    public Long getId() {
        if (member != null) {
            return member.getId();
        }
        return null;
    }

    // 可以添加更多获取会员信息的方法

    // 如果你需要确保在会话结束时执行某些清理工作，可以实现DisposableBean接口或者使用@PreDestroy注解
    @PreDestroy
    public void cleanup() {
        // 执行清理工作，如日志记录等
        System.out.println("LoginMemberHolder is being destroyed.");
    }
}
